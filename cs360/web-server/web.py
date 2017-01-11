"""
A TCP server that handles multiple clients with polling.  Typing
Control-C will quit the server.
"""

import argparse

from poller import Poller

class Main:
    """ Parse command line options. """
    def __init__(self):
        self.parse_arguments()

    def parse_arguments(self):
        ''' parse arguments, which include '-p' for port, and '-d' for debug '''
        parser = argparse.ArgumentParser(prog='Web Server', description='A simple server that handles multiple clients', add_help=True)
        parser.add_argument('-p', '--port', type=int, action='store', help='port the server will bind to',default=8080)
	parser.add_argument('-d', '--debug', action='store_true', help='print debug statements', default=False)
        self.args = parser.parse_args()

    def run(self):
        p = Poller(self.args.port,self.args.debug)
        p.run()

if __name__ == "__main__":
    m = Main()
    try:
        m.run()
    except KeyboardInterrupt:
        pass
