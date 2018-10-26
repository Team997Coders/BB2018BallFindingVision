import java.util.ArrayList;

import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.tables.*;
import edu.wpi.cscore.*;
import edu.wpi.cscore.HttpCamera.HttpCameraKind;

import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import com.beust.jcommander.*;

public class Main {

  // command line args
  @Parameter(names={"--team", "-t"}, required=true, description="FIRST team number")
  int team;
  @Parameter(names={"--nthost", "-h"},  
    description="NetworkTables server host IP address (for testing)")
  String ntHost = "";
  @Parameter(names={"--nont", "-n"},  
    description="Do not call out to network tables to write interpreted values")
  boolean noNT = false;
  @Parameter(names={"--cameraurl", "-c"},  
    description="Use specified MJPEG over http streaming source (overrides NetworkTables value)")
  String cameraURL = "";
  @Parameter(names = "--help", help = true)
  boolean help = false;

  public static void main(String ... argv) {
      Main main = new Main();
      // parse command line args
      JCommander jc = JCommander.newBuilder()
        .programName("CameraVision")
        .addObject(main)
        .build();

      try {
        jc.parse(argv);
        if (main.help) {
          jc.usage();
        } else {
          // run the app
          main.run();
        }
      } catch (ParameterException pe)
      {
        // print the parameter error, show the usage, and bail
        System.out.println(pe.getMessage());
        jc.usage();
        System.exit(1);        
      }
  }

  public void run() {
    // Loads our OpenCV library. This MUST be included
    System.loadLibrary("opencv_java310");

    if (!noNT) {
      NetworkTable.setClientMode();
      NetworkTable.setTeam(team);
      if (ntHost != "") {
        NetworkTable.setIPAddress(ntHost);
      }
      NetworkTable.initialize();
    }

    // This is the network port you want to stream the raw received image to
    // By rules, this has to be between 1180 and 1190, so 1185 is a good choice
    int streamPort = 1185;

    // This streaming mjpeg server will allow you to see the source image in a browser.
    MjpegServer inputStream = new MjpegServer("MJPEG Server", streamPort);

    // Selecting a Camera
    // Uncomment one of the 2 following camera options
    // The top one receives a stream from another device, and performs operations based on that
    // On windows, this one must be used since USB is not supported
    // The bottom one opens a USB camera, and performs operations on that, along with streaming
    // the input image so other devices can see it.

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
    HttpCamera camera = setHttpCamera(cameraName, inputStream);
    
    /***********************************************/

    /*
    // USB Camera
    // This gets the image from a local USB camera (does NOT work on Windows...sucks!) 
    // Usually this will be on device 0, but there are other overloads
    // that can be used
    UsbCamera camera = setUsbCamera(0, inputStream);
    */
    // Set the resolution for our camera, since this is over USB
    camera.setResolution(640,480);

    // This creates a CvSink for us to use. This grabs images from our selected camera, 
    // and will allow us to use those images in opencv
    CvSink imageSink = new CvSink("CV Image Grabber");
    imageSink.setSource(camera);

    // This creates a CvSource to use.
    // This will take in a Mat image that has had OpenCV operations. 
    CvSource imageSource = new CvSource("CV Image Source", VideoMode.PixelFormat.kMJPEG, 640, 480, 30);
    // This streaming mjpeg server will allow you to see the final image processed image in a browser.
    MjpegServer cvStream = new MjpegServer("CV Image Stream", 1186);
    cvStream.setSource(imageSource);

    // All Mats and Lists should be stored outside the loop to avoid allocations
    // as they are expensive to create
    Mat inputImage = new Mat();
    Mat outputImage1 = new Mat();
    Mat outputImage2 = new Mat();
    // This duplication could be ditched with a common interface, but then the
    // generated pipelines would have to be modified.
    BlueBallGripPipeline bluePipeline = new BlueBallGripPipeline();
    BlueBallGripPipelineInterpreter blueInterpreter = new BlueBallGripPipelineInterpreter(bluePipeline);
    RedBallGripPipeline redPipeline = new RedBallGripPipeline();
    RedBallGripPipelineInterpreter redInterpreter = new RedBallGripPipelineInterpreter(redPipeline);
    NetworkTable publishingTable = NetworkTable.getTable("SmartDashboard");
    System.out.println("Processing stream...");
    
    // Infinitely process image
    while (true) {
      // Grab a frame. If it has a frame time of 0, there was an error.
      // Just skip and continue
      long frameTime = imageSink.grabFrame(inputImage);
      if (frameTime == 0) {
        System.out.println(imageSink.getError());
        continue;
      }

      // Apply the pipeline to the image.
      bluePipeline.process(inputImage);
      redPipeline.process(inputImage);

      // Update network table
      if (!noNT) {
        publishingTable.putBoolean("BlueBallFound", blueInterpreter.ballsFound());
        publishingTable.putNumber("BlueBallCount", blueInterpreter.ballCount());
        publishingTable.putBoolean("RedBallFound", redInterpreter.ballsFound());
        publishingTable.putNumber("RedBallCount", redInterpreter.ballCount());
      }

      // Write a processed image that you want to restream
      // This is a marked up image of what the camera sees
      Features2d.drawKeypoints(
        inputImage, 
        bluePipeline.findBlobsOutput(), 
        outputImage1, 
        new Scalar(2,254,255),              // yellowish circle 
        Features2d.DRAW_RICH_KEYPOINTS);    // draws a full-sized circle around found point(s)
      Features2d.drawKeypoints(
        outputImage1, 
        redPipeline.findBlobsOutput(), 
        outputImage2, 
        new Scalar(2,254,255),              // yellowish circle 
        Features2d.DRAW_RICH_KEYPOINTS);    // draws a full-sized circle around found point(s)
      
      // Ident blue balls on image
      for (KeyPoint k : bluePipeline.findBlobsOutput().toArray()) {
        Imgproc.putText(outputImage2, "Blue", k.pt, Core.FONT_HERSHEY_COMPLEX_SMALL, .75, new Scalar(2,254,255));
      }

      // Ident red balls on image
      for (KeyPoint k : redPipeline.findBlobsOutput().toArray()) {
        Imgproc.putText(outputImage2, "Red", k.pt, Core.FONT_HERSHEY_COMPLEX_SMALL, .75, new Scalar(2,254,255));
      }
      
      // Spit the image out to the stream.
      imageSource.putFrame(outputImage2);
    }
  }

  private HttpCamera setHttpCamera(String cameraName, MjpegServer server) {
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
            // TODO Auto-generated catch block
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

  /*
  private static UsbCamera setUsbCamera(int cameraId, MjpegServer server) {
    // This gets the image from a USB camera 
    // Usually this will be on device 0, but there are other overloads
    // that can be used
    UsbCamera camera = new UsbCamera("CoprocessorCamera", cameraId);
    server.setSource(camera);
    return camera;
  }
  */
}