import java.util.*;
import java.util.concurrent.*;

import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;
import edu.wpi.cscore.*;
import edu.wpi.cscore.HttpCamera.HttpCameraKind;

import org.opencv.core.*;

public class Main {

  public static void main(String ... argv) {
    Main main = new Main();
    RuntimeSettings runtimeSettings = new RuntimeSettings(argv);
    if (runtimeSettings.parse()) {
      if (runtimeSettings.getHelp()) {
        // print out the usage to sysout
        runtimeSettings.printUsage();
      } else {
        // run the app
        main.run(runtimeSettings);
        System.exit(0);
      }
    } else {
      // print the parameter error, show the usage, and bail
      System.err.println(runtimeSettings.getParseErrorMessage());
      runtimeSettings.printUsage();
      System.exit(1);
    }
  }

  public void run(RuntimeSettings runtimeSettings) {
    // Loads our OpenCV library. This MUST be included
    System.loadLibrary("opencv_java310");

    NetworkTable publishingTable = null;

    if (!runtimeSettings.getNoNT()) {
      NetworkTable.setClientMode();
      NetworkTable.setTeam(runtimeSettings.getTeam());
      if (runtimeSettings.getNTHost() != "") {
        NetworkTable.setIPAddress(runtimeSettings.getNTHost());
      }
      NetworkTable.initialize();
      publishingTable = NetworkTable.getTable("SmartDashboard");
    }

    // This is the network port you want to stream the raw received image to
    // By rules, this has to be between 1180 and 1190, so 1185 is a good choice
    int streamPort = 1185;

    // This streaming mjpeg server will allow you to see the source image in a browser.
    MjpegServer inputStream = new MjpegServer("MJPEG Server", streamPort);

    // HTTP Camera
    // This is our camera name from the robot.
    // This can be set in your robot code with the following command
    // CameraServer.getInstance().startAutomaticCapture("YourCameraNameHere");
    // "USB Camera 0" is the default if no string is specified
    // In NetworkTables, you can create a key CameraPublisher/<YourCameraNameHere>/streams
    // of an array of strings to store the urls of the stream(s) the camera publishes.
    // These urls point to an mjpeg stream over http, with each jpeg image separated
    // into multiparts with the mixed data sub-type.
    // See https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html for more info.
    // Jpeg part delimiters are separated by a boundary string specified in the Content-Type header.
    //String cameraName = "USB Camera 0";
    String cameraName = "VisionCoProc";
    HttpCamera camera = setHttpCamera(cameraName, inputStream, runtimeSettings.getCameraURL(), runtimeSettings.getNoNT());
    
    /***********************************************/

    // This creates a CvSink for us to use. This grabs images from our selected camera, 
    // and will allow us to use those images in opencv
    CvSink imageSink = new CvSink("CV Image Grabber");
    imageSink.setSource(camera);

    // This creates a CvSource to use.
    // This will take in a Mat image that has had OpenCV operations. 
    CvSource imageSource = new CvSource(
      "CV Image Source", 
      VideoMode.PixelFormat.kMJPEG, 
      camera.getVideoMode().width, 
      camera.getVideoMode().height, 
      camera.getVideoMode().fps);
    // This streaming mjpeg server will allow you to see the final image processed image in a browser.
    MjpegServer cvStream = new MjpegServer("CV Image Stream", 1186);
    cvStream.setSource(imageSource);

    // Set up the image pump to grab images in a separate thread.
    ImagePump imagePump = new ImagePump(imageSink);

    // Wire up image processing components
    IBallPipeline blueBallPipeline = new BlueBallPipeline();
    ImageProcessor blueBallImageProcessor = 
      new ImageProcessor(
        blueBallPipeline, 
        new BlueBallNetworkTableWriter(
          new BallPipelineInterpreter(blueBallPipeline), 
          publishingTable));

    IBallPipeline redBallPipeline = new RedBallPipeline();
    ImageProcessor redBallImageProcessor = 
      new ImageProcessor(
        redBallPipeline, 
        new RedBallNetworkTableWriter(
          new BallPipelineInterpreter(redBallPipeline), 
          publishingTable));

    Mat inputImage = new Mat();
    Mat outputImage = new Mat();
    Mat outputImage2 = new Mat();

    System.out.println("Processing stream...");

    // Prime the image pump
    try {
      inputImage = imagePump.pump().get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      System.out.println(e.getMessage());
    }

    while (!Thread.currentThread().isInterrupted()) {
      if (!inputImage.empty()) {
        // Process the image looking for respective color balls...concurrently
        Future<Void> redBallImageProcessorFuture = redBallImageProcessor.process(inputImage);
        Future<Void> blueBallImageProcessorFuture = blueBallImageProcessor.process(inputImage);
        Future<Mat> nextImageFuture = imagePump.pump();
        try {
          redBallImageProcessorFuture.get();
          blueBallImageProcessorFuture.get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          System.out.println(e.getMessage());
        }

        // Annotate the image
        outputImage = redBallImageProcessor.annotate(inputImage);
        outputImage2 = blueBallImageProcessor.annotate(outputImage);

        // Write out the image
        imageSource.putFrame(outputImage2);

        // Get the next image
        try {
          inputImage = nextImageFuture.get();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          System.out.println(e.getMessage());
        }
      }
    }
  }

  private HttpCamera setHttpCamera(String cameraName, MjpegServer server, String cameraURL, boolean noNT) {
    // If the camera URL is explicitly specified on the command line, then use it.
    if (cameraURL != "") {
      HttpCamera camera = null;
      camera = new HttpCamera("CoprocessorCamera", cameraURL);
      server.setSource(camera);
      return camera;
    } else if (!noNT) {   // get the camera URL from network tables
      // Start by grabbing the camera from NetworkTables
      NetworkTable publishingTable = NetworkTable.getTable("CameraPublisher");
      // Wait for robot to connect. Allow this to be attempted indefinitely
      while (true) {
        try {
          if (publishingTable.getSubTables().size() > 0) {
            break;
          }
          Thread.sleep(500);
          } catch (Exception e) {
              e.printStackTrace();
          }
      }


      HttpCamera camera = null;
      if (!publishingTable.containsSubTable(cameraName)) {
        return null;
      }
      ITable cameraTable = publishingTable.getSubTable(cameraName);
      String[] urls = cameraTable.getStringArray("streams", null);
      if (urls == null) {
        return null;
      }
      ArrayList<String> fixedUrls = new ArrayList<String>();
      for (String url : urls) {
        if (url.startsWith("mjpg")) {
          fixedUrls.add(url.split(":", 2)[1]);
        }
      }
      System.out.println(fixedUrls.toString());
      camera = new HttpCamera("CoprocessorCamera", fixedUrls.toArray(new String[0]));
      server.setSource(camera);
      return camera;
    }
    // It is possible for the camera to be null. If it is, that means no camera could
    // be found using NetworkTables to connect to.  And, user did not specify one on command line.
    // Create an HttpCamera by giving a specified stream
    // Note if this happens, no restream will be created.
    // We assume that you have started up a local mjpeg stream.
    System.out.println("Using hardcoded local http streaming camera...");
    HttpCamera camera = null;
    camera = new HttpCamera("CoprocessorCamera", 
      "http://127.0.0.1:1337/mjpeg_stream", 
      HttpCameraKind.kMJPGStreamer);
    server.setSource(camera);
    return camera;
  }
}