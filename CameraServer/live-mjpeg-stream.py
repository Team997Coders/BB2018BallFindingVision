#!/usr/bin/python3
# based on the ideas from http://synack.me/blog/implementing-http-live-streaming
# lifted from https://gist.github.com/sakti/4761739
# Note that a few bugs have been fixed: Content-Type caps and boundary dashes removed - CCB
# Also, upgraded to run python3 (python2 will not longer work)
# Run this script and then launch the following pipeline:
# gst-launch videotestsrc pattern=ball ! video/x-raw-rgb, framerate=15/1, width=640, height=480 !  jpegenc ! multipartmux boundary=spionisto ! tcpclientsink port=9999

import sys
from queue import Queue
from threading import Thread
from socket import socket
from select import select
from wsgiref.simple_server import WSGIServer, make_server, WSGIRequestHandler
from socketserver import ThreadingMixIn


class MyWSGIServer(ThreadingMixIn, WSGIServer):
     pass 

def create_server(host, port, app, server_class=MyWSGIServer,  
          handler_class=WSGIRequestHandler):
     return make_server(host, port, app, server_class, handler_class) 

INDEX_PAGE = """
<html>
<head>
    <title>Gstreamer testing</title>
</head>
<body>
<h1>Live camera with GStreamer</h1>
<img src="/mjpeg_stream"/>
<hr />
</body>
</html>
"""
ERROR_404 = """
<html>
  <head>
    <title>404 - Not Found</title>
  </head>
  <body>
    <h1>404 - Not Found</h1>
  </body>
</html>
"""


class IPCameraApp(object):
    queues = []

    def __call__(self, environ, start_response):    
        if environ['PATH_INFO'] == '/':
            start_response("200 OK", [
                ("Content-Type", "text/html"),
                ("Content-Length", str(len(INDEX_PAGE.encode("utf-8"))))
            ])
            return iter([INDEX_PAGE.encode("utf-8")])    
        elif environ['PATH_INFO'] == '/mjpeg_stream':
            return self.stream(start_response)
        else:
            start_response("404 Not Found", [
                ("Content-Type", "text/html"),
                ("Content-Length", str(len(ERROR_404.encode("utf-8"))))
            ])
            return iter([ERROR_404.encode("utf-8")])

    def stream(self, start_response):
        start_response('200 OK', [('Content-Type', 'multipart/x-mixed-replace; boundary=spionisto')])
        q = Queue()
        self.queues.append(q)
        while True:
            try:
                yield q.get()
            except:
                if q in self.queues:
                    self.queues.remove(q)
                return


def input_loop(app):
    sock = socket()
    sock.bind(('', 9999))
    sock.listen(1)
    while True:
        print('Waiting for input stream')
        sd, addr = sock.accept()
        print ('Accepted input stream from', addr)
        data = True
        while data:
            readable = select([sd], [], [], 0.1)[0]
            for s in readable:
                data = s.recv(1024)
                if not data:
                    break
                for q in app.queues:
                    q.put(data)
        print ('Lost input stream from', addr)

if __name__ == '__main__':

    #Launch an instance of wsgi server
    app = IPCameraApp()
    port = 1337
    print ('Launching camera server on port', port)
    httpd = create_server('', port, app)

    print ('Launch input stream thread')
    t1 = Thread(target=input_loop, args=[app])
    t1.setDaemon(True)
    t1.start()

    try:
        print ('Httpd serve forever')
        httpd.serve_forever()
    except KeyboardInterrupt:
        print ("Shutdown camera server ...")

    sys.exit(0)
