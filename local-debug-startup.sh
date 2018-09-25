#!/bin/bash
#
# The first parameter is the device index of the camera to grab.
# Linux assigns them in an unpredictable manner.  Default to video0
#
python ntserver.py &
pause 3
python live-mjpeg-stream.py &
pause 3
./stream-camera.sh $1 &
pause 3