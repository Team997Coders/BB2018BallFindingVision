#!/bin/bash
#
# The first parameter is the device index of the camera to grab.
# Linux assigns them in an unpredictable manner.  Default to video0
#
~/.local/bin/ntserver &
sleep 3
~/.local/bin/ipcamera &
sleep 3
./stream-camera.sh $1 &
sleep 3