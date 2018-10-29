import edu.wpi.first.wpilibj.networktables.*;

import org.opencv.core.*;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.*;

public class ProcessImages implements Runnable {
    private volatile boolean keepRunning = true;
    private CvSink imageSink;
    private CvSource imageSource;
    private NetworkTable publishingTable;

    public ProcessImages(CvSink imageSink, CvSource imageSource, NetworkTable publishingTable) {
        // TODO: Check for null
        this.imageSink = imageSink;
        // TODO: Check for null
        this.imageSource = imageSource;
        this.publishingTable = publishingTable;
    }

    public void requestThreadStop() {
        keepRunning = false;
    }

    public void run()
    {
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

        while(keepRunning) {
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
            if (publishingTable != null) {
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
}