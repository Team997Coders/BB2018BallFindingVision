REM
REM Requires Python 3 and gstream to be installed
REM
REM The first parameter is the device index of the camera to grab.
REM Windows assigns them in an unpredictable manner.  A laptop's built in camera
REM is usually device index 0 but if a USB camera is plugged in at boot, it
REM might be camera 0.
REM
start ./python/scripts/python ntserver.py
timeout 3
start ./python/scripts/python live-mjpeg-stream.py
timeout 3
start stream-camera.bat %1
timeout 3
start runCameraVision.bat
