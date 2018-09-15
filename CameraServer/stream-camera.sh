#!/bin/bash
gst-launch-1.0 v4l2src ! image/jpeg, framerate=30/1, width=640, height=480 ! queue ! multipartmux boundary=spionisto ! queue leaky=2 ! tcpclientsink host=127.0.0.1 port=9999
