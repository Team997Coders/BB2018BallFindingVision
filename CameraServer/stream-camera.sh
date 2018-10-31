#!/bin/bash
gst-launch-1.0 v4l2src device=/dev/video${1:-0} ! image/jpeg, framerate=30/1, width=320, height=240 ! queue ! multipartmux boundary=spionisto ! queue leaky=2 ! tcpclientsink host=127.0.0.1 port=9999
