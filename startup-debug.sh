#!/bin/bash
#
# The first parameter is the device index of the camera to grab.
# Linux assigns them in an unpredictable manner.  Default to video0
#
python3 ntserver.py &
sleep 3
python3 live-mjpeg-stream.py &
sleep 3
./stream-camera.sh $1 &
sleep 3
./runCameraVision-debug.sh &
