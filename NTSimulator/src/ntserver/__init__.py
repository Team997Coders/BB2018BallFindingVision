#!/usr/bin/env python3
#
# This is a NetworkTables server (eg, the robot or simulator side).
#
# On a real robot, you probably would create an instance of the
# wpilib.SmartDashboard object and use that instead -- but it's really
# just a passthru to the underlying NetworkTable object.
#
# Put the camera details in a table as the wpilib CameraServer class would do.
# Our coprocessor program will then read those details.  Usually, the
# coprocessor board will have it's camera hooked up locally, but this
# allows it to be connected anywhere.
# Next, set up a listener to report on changes to the SmartDashboard table.
# This will enable us to see vision processing results as the robot will see them.
#

import time
from networktables import NetworkTables
import logging

def valueChanged(table, key, value, isNew):
    print("valueChanged: key: '%s'; value: %s; isNew: %s" % (key, value, isNew))

def runForever():
    # To see messages from networktables, you must setup logging
    logging.basicConfig(level=logging.DEBUG)

    NetworkTables.initialize()
    # TODO: Make this read in a JSON file to init NT database
    # TODO: Can we make a simple web server to display NT contents?
    sd = NetworkTables.getTable("SmartDashboard")
    camera = NetworkTables.getTable("CameraPublisher/VisionCoProc")
    camera.putStringArray('streams', ['mjpg:http://visioncoproc.local:1337/mjpeg_stream'])
    sd.addEntryListener(valueChanged)
        
    while True:
        time.sleep(1)
