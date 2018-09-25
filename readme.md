# Bunny Bot 2018 sample vision system

This is a sample project based on the [WPILib sample build system](https://github.com/wpilibsuite/VisionBuildSamples) for building Java based vision targeting for running on systems other than the roboRIO. This currently supports the following target platforms with a Windows development environment:

* Windows
* Raspberry Pi running Raspbian
* Generic Armhf devices (such as the BeagleBone Black or the Jetson) - not tested

## Choosing which system to build for
As there is no way to autodetect which system you want to build for, such as building for a Raspberry Pi on a windows desktop, you have to manually select which system you want to build for.
To do this, open the `build.gradle` file. Near the top at line 10 starts a group of comments explaining what to do. For a basic rundown, there are 3 lines that start with `ext.buildType =`. 
To select a device, just uncomment the system you want to build for. 

Note it is possible to easily switch which system you want to target. To do so, just switch which build type is uncommented. When you do this, you will have to run a clean `gradlew clean` in order to
clear out any old artifacts. 

## Choosing the camera type
The original WPILib sample only supported getting camera input from the roboRio, which is the only method supported when running vision processing on Windows.  This made testing on a Windows platform impractical.  It did support direct USB connect, but only via raspbian.  So you could debug on Windows, but you had to remote run the application on raspbian.  Again, troublesome.

Instead, this sample has been expanded to use a utility [gstreamer](https://gstreamer.freedesktop.org) to grab output from a USB connected camera from either platform.

Further, an MJPEG streaming server is included to make it possible for the image processing application to simply reference a streaming source over HTTP using the WPILib HttpCamera class, even if you do not have a network camera.  This streaming source can be from a local USB camera, or you can offload image streaming to a another device and separate image processing from streaming.  See the CameraServer sub-project.

## Building and running on the local device
You can run `gradlew build` to run a build for a Windows target.

When doing this, the output files will be placed into `output\`. From there, you can run either the .bat file on windows or the shell script on unix in order to run your project.

You can also run the project from the VSCode debugger locally and remotely using the built-in task and launch settings. (TODO: Put more in about this)

## Building for another platform
If you are building for another platform, trying to run `gradlew build` will not work, as tests will not run on Windows targeting another platform.  You can run `gradlew build -x test` to ignore tests.

In that case, when you run the build, a zip file
is placed in `output\`. This zip contains the built jar, the OpenCV library for your selected platform, and either a .bat file or shell script to run everything. All you have to do is copy
this file to the system, extract it, then run the .bat or shell script to run your program.

Finally, if buildType is targeting raspbian, a `gradlew deploy -x test` task exists to deploy built project automatically to a raspberry pi.  No dependencies are required to be installed on the pi other than the stretch distro.  The deploy task will automatically install them. (TODO: Talk more about the settings on connecting to pi.)

## What this gives you
You can develop and test a WPILib image processing application without needing a robot.  You can develop develop and test on Windows and then deploy on a raspberry pi.  You can use a USB camera on either platform locally, or you can offload image capture to another device and not change image processing code.

JUnit and Mockito have been used to demonstrate automated testing of vision processing application, which factors out external dependencies (like having to have a camera plugged in).

This complete sample gets an image from a local camera stream. It then restreams the input image in it's raw form in order to make it viewable on another system.
It then creates an OpenCV sink from the camera, which allows us to grab OpenCV images. It then creates an output stream for an OpenCV image, for instance so you can stream an annotated
image. The default sample attempts to identify a blue raquetball for the 2018 Bunny Bot game. In addition, a [NetworkTables simultated server](https://github.com/robotpy/pynetworktables) is set up, so you can send data regarding the targets to a server that simulates a robot.  A command line arg enables you to change this setting. (TODO: Document them here)

## Other configuration options
The build script provides a few other configuration options. These include selecting the main class name, and providing an output name for the project.
Please see the `build.gradle` file for where to change these. 