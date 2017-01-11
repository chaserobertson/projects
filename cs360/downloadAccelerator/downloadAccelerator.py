#! /usr/bin/env python3

import sys
import argparse
import requests
import threading
import time

class myThread (threading.Thread):
    def __init__(self, threadID, byteRange, url):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.byteRange = byteRange
        self.url = url
        self.content = bytes()
    def run(self):
        begin = self.byteRange[0]
        end = self.byteRange[-1]
        headers = {}
        headers['Range'] = 'bytes={}-{}'.format(begin,end)
        headers['Accept-Encoding'] = 'identity'
        r = requests.get(self.url, headers=headers)
        self.content = r.content

def writeToFile(threads, url):
	content = bytes()
	for t in threads:
		content += t.content
	url = url.split('/')
	if(url[-1] == ''):
		file = open('index.html','w')
	else:
		file = open(url[-1],'wb')
	file.write(content)

def download(url, numthreads):
	head = requests.head(url)
	length = int(head.headers['Content-Length'])
	if length < 1:
		print('Invalid Content-Length')
		sys.exit()
	interval = length // numthreads
	threads = []
	start = time.time()
	for i in range(numthreads):
		byteRange = range(i*interval, (i+1)*interval, 1) #each thread gets interval bytes
		if i == (numthreads - 1):
			byteRange = range(i*interval, length, 1) #final thread picks up extra from int division
		threads.append(myThread(i,byteRange,url))
		threads[i].start()
	for t in threads:
		t.join()
	end = time.time()
	writeToFile(threads, url)
	seconds = end - start
	print('{} {} {} {}'.format(url, numthreads, length, seconds))

def main():
	print()
	parser = argparse.ArgumentParser()
	parser.add_argument("-n", "--numthreads", type=int, help="number of threads")
	parser.add_argument("url", type=str, help="download the page at the given url")
	args = parser.parse_args()
	numthreads = args.numthreads
	if numthreads == None or numthreads < 1:
		numthreads = 1
	url = args.url
	download(url, numthreads)
	print()

main()
