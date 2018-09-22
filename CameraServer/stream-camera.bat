gst-launch-1.0 ksvideosrc device-index=1 ! image/jpeg, framerate=30/1, width=640, height=480 ! queue ! multipartmux boundary=spionisto ! queue leaky=2 ! tcpclientsink host=127.0.0.1 port=9999
