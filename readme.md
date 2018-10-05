# Bunny Bot 2018 sample vision system

This is a sample project based on the [WPILib sample build system](https://github.com/wpilibsuite/VisionBuildSamples) for building Java based vision targeting for running on systems other than the roboRIO. The object of this project is to make it easy to design, develop, code, test, and deploy FIRST WPILib vision processing apps from a Windows workstation. This project currently supports the following target platforms with a Windows development environment:

* Windows
* Raspberry Pi running Raspbian
* Generic Armhf devices (such as the BeagleBone Black or the Jetson) - not tested

## Windows development system requirements
* [VSCode](https://code.visualstudio.com/download)
  * Plugins
    * Language Support for Java by Red Hat
    * Python
* [Git](https://git-scm.com/downloads)
  * Lots of setup dialogs.  Just take all the defaults.
* [Python3](https://www.python.org/downloads/)
  *  Be sure to add Python to path.  It is an unchecked option on the install dialog.
  *  pip (automatic for Windows)
* [Java 8](https://developers.redhat.com/products/openjdk/download/)
* [gstreamer](https://gstreamer.freedesktop.org/download/)
  * Run the complete install instead of the typical install.
* [Bonjour](https://support.apple.com/kb/DL999?locale=en_US)
  * Optional.  If you want to access roboRio or raspberry pi by hostname without registering in DNS.  If you have the driver station installed, Bonjour protocol is already installed.
  
## Choosing which system to build for
As there is no way to autodetect which system you want to build for, such as building for a Raspberry Pi on a windows desktop, you have to manually select which system you want to build for.
To do this, use the `-Ptarget="target"` parameter for the gradle build.  The valid targets are `windows (the default), arm-raspbian, armhf`.  So for example, to build for the raspberry pi, use `gradlew build -Ptarget="arm-raspbian"`. 

When you change targets, you should run a clean `gradlew clean` in order to
clear out any old artifacts from other targets.

## Choosing the camera type
The original WPILib sample only supported getting camera input from the roboRio when running vision processing on Windows.  This made developing/testing on a Windows platform impractical.  It did support direct USB connect, but only via raspbian.  So you could debug on Windows, but you had to remote run the application on raspbian.  Again, troublesome.

Instead, this sample has been expanded to use a utility [gstreamer](https://gstreamer.freedesktop.org) to grab output from a USB connected camera from either platform.

Further, an MJPEG streaming server is included to make it possible for the image processing application to simply reference a streaming source over HTTP using the WPILib HttpCamera class, even if you do not have a network camera.  This streaming source can be from a local USB camera, or you can offload image streaming to a another device and separate image processing from streaming.  See the CameraServer sub-project.

## Building and running on the local development workstation
You can run `gradlew build` to run a build for a Windows target.

When doing this, the output files will be placed into `bin\`. From there, you can run either the .bat file on windows or the shell script on unix in order to run your project.  Note that Linux workstations have not been tested yet, but should work.

You can also run the project from the VSCode debugger locally and remotely using the built-in task and launch settings. (TODO: Put more in about this)

## Building for another platform
If you are building for another platform, trying to run `gradlew build -Ptarget=<target>` will not work, as tests will not run on Windows targeting another platform.  You can run `gradlew build -x test -Ptarget=<target>` to ignore tests.

In that case, when you run the build, runtimes for the target selected will be placed in `bin\`. All you have to do is copy this file to the target system, excluding the virtual python directory, then run the .bat or shell script to run your program.  You will also need to install the python packages built in the CameraServer and NTSimulator project directories `./build/python/dist`.  Those packages can be installed with `pip3 install -U <package.zip>`.

Finally, if buildType is targeting raspbian, a `gradlew deploy -x test -Ptarget="arm-raspbian"` task exists to build and deploy the project automatically to a raspberry pi.  No dependencies are required to be installed on the pi other than the stretch distro.  The deploy task will automatically install them. (TODO: Talk more about the settings on connecting to pi.)

## What this gives you
You can develop and test a WPILib image processing application without needing a robot.  You can develop and test on Windows and then deploy on a raspberry pi.  You can use a USB camera on either platform locally, or you can offload image capture to another device and not change image processing code.

JUnit and Mockito have been used to demonstrate automated testing of vision processing application, which factors out external dependencies (like having to have a camera plugged in).

This complete sample gets an image from a local camera stream. It then restreams the input image in it's raw form in order to make it viewable on another system.
It then creates an OpenCV sink from the camera, which allows us to grab OpenCV images. It then creates an output stream for an OpenCV image, for instance, so you can stream an annotated image. The default sample attempts to identify a blue raquetball for the 2018 Bunny Bot game. In addition, a [NetworkTables simultated server](https://github.com/robotpy/pynetworktables) is set up, so you can send data regarding the targets to a server that simulates a robot.  A command line arg enables you to change this setting. (TODO: Document them here)

## Other configuration options
The build script provides a few other configuration options. These include selecting the main class name, and providing an output name for the project.
Please see the `build.gradle` file for where to change these. 