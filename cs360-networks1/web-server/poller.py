import errno
import select
import socket
import sys
import traceback
from email.utils import formatdate

class Poller:
    """ Polling server """
    def __init__(self,port,debug):
        self.host = ""
        self.port = port
	self.debug = debug
        self.open_socket()
        self.clients = {}
	self.data = {}
        self.size = 1024
	self.path = ""
	self.media = {}
	self.timeout = 1
        self.configure()

    def configure(self):
	f = open('web.conf', 'r')
	for line in f:
	    line = line.split()
	    if len(line) != 3:
		continue
	    elif line[0] == 'host':
		self.host = line[1]
		self.path = line[2]
	    elif line[0] == 'media':
		self.media[line[1]] = line[2]
	    elif line[0] == 'parameter':
		if line[1] == 'timeout': 
		    self.timeout = float(line[2])
	if self.debug:
	    print 'host: {}'.format(self.host)
	    print 'path: {}'.format(self.path)
	    print 'media: {}'.format(self.media)
	    print 'timeout: {}'.format(self.timeout)

    def open_socket(self):
        """ Setup the socket for incoming clients """
        try:
            self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
            self.server.bind((self.host,self.port))
            self.server.listen(5)
            self.server.setblocking(0)
        except socket.error, (value,message):
            if self.server:
                self.server.close()
            print "Could not open socket: " + message
            sys.exit(1)

    def run(self):
        """ Use poll() to handle each incoming client."""
        self.poller = select.epoll(1)
        self.pollmask = select.EPOLLIN | select.EPOLLHUP | select.EPOLLERR
        self.poller.register(self.server,self.pollmask)
        while True:
            # poll sockets
            try:
		#if self.debug: print 'timeout set to {}'.format(self.timeout)
                fds = self.poller.poll(timeout=self.timeout)
            except:
                return
            for (fd,event) in fds:
                # handle errors
                if event & (select.POLLHUP | select.POLLERR):
                    self.handleError(fd)
                    continue
                # handle the server socket
                if fd == self.server.fileno():
                    self.handleServer()
                    continue
                # handle client socket
                result = self.handleClient(fd)

    def handleError(self,fd):
        self.poller.unregister(fd)
        if fd == self.server.fileno():
            # recreate server socket
            self.server.close()
            self.open_socket()
            self.poller.register(self.server,self.pollmask)
        else:
            # close the socket
            self.clients[fd].close()
	    del self.data[fd]
            del self.clients[fd]

    def handleServer(self):
        # accept as many clients as possible
        while True:
            try:
                (client,address) = self.server.accept()
            except socket.error, (value,message):
                # if socket blocks because no clients are available,
                # then return
                if value == errno.EAGAIN or errno.EWOULDBLOCK:
                    return
                print traceback.format_exc()
                sys.exit()
            # set client socket to be non blocking
            client.setblocking(0)
            self.clients[client.fileno()] = client
	    self.data[client.fileno()] = ''
            self.poller.register(client.fileno(),self.pollmask)

    def handleClient(self,fd):
        try:
            self.data[fd] += self.clients[fd].recv(self.size)
        except socket.error, (value,message):
            # if no data is available, move on to another client
            if value == errno.EAGAIN or errno.EWOULDBLOCK:
                return
            print traceback.format_exc()
            sys.exit()

        if self.data[fd]:
	    if self.data[fd].find('\r\n\r\n') != -1:
		self.sendResponse(self.clients[fd], self.data[fd])
        else:
            self.poller.unregister(fd)
            self.clients[fd].close()
            del self.clients[fd]
	    del self.data[fd]
	    if self.debug: print 'timed out client'

    def sendResponse(self, client, data):
        try:
            from http_parser.parser import HttpParser
        except ImportError:
            from http_parser.pyparser import HttpParser

        p = HttpParser()
	m = data.split()[0]
        nparsed = p.execute(data,len(data))
        if(self.debug):
	    print data
            #print p.get_method(),p.get_path(),p.get_headers()
	path = '/'
	if p.get_path() == path:
	    path = '/index.html'
	else:
	    path = p.get_path()
	try:
	    if self.debug: print 'opening {}'.format(path[1:])
	    f = open(path[1:], 'r')
	except IOError as e:
	    if self.debug: print 'file could not be opened'
	    f = None
	response = self.formatResponse(f, path, p, m)
	try:
	    client.sendall(response)
	except IOError as e:
	    print 'error sending response: '+e.traceback

    def formatResponse(self, file, path, p, m):
	date = formatdate(timeval=None, localtime=False, usegmt=True)
	body = ''
	content_type = ''

	if m=='POST' or m=='DELETE' or m=='PUT' or m=='OPTIONS' or m=='TRACE' or m=='CONNECT':
	    code = 501
	    phrase = 'Not Implemented'
	elif (m != 'GET' and m != 'HEAD') or p.get_path() == '' or len(p.get_headers()) < 1:
	    code = 400
	    phrase = 'Bad Request'
	elif file == None:
	    try:
		open(path[1:], 'r')
	    except IOError as e:
		if e.errno == 2: #2 if not there, 13 if permission
		    code = 404
		    phrase = 'Not Found'
		elif e.errno == 13:
		    code = 403
		    phrase = 'Forbidden'
		else:
		    code = 500
		    phrase = 'Internal Server Error'
	else:
	    code = 200
	    phrase = 'OK'
	    if m == 'GET': 
		range_header = p.get_headers().get('Range',None)
		if range_header == None: body = file.read()
		else:
		    code = 206
		    range_header = range_header.split('=')
		    range_header = range_header[-1].split('-')
		    if self.debug: print 'sending bytes {} thru {}'.format(range_header[0],range_header[1])
		    body = file.read()
		    body = body[int(range_header[0]):int(range_header[1])+1]
	    file_type = path[path.find('.')+1:]
	    content_type = self.media[file_type]

	if code != 200 and code != 206: 
	    body = phrase
	    content_type = 'text/html'

	response = 'HTTP/1.1 '+str(code)+' '+str(phrase)+'\r\n'
	response += 'Date: '+date+'\r\n'
	response += 'Server: me/1.0 (Ubuntu)\r\n'
	response += 'Content-Type: '+content_type+'\r\n'
	response += 'Content-Length: '+str(len(body))+'\r\n'
	response += 'Last-Modified: '+date+'\r\n'
	response += '\r\n'
	if self.debug: print response
	response += body

	return response

