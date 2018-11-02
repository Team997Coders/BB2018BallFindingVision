import java.io.*;
import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Test the blue ball pipeline to make sure it detects ball properly.
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class BlueBallGripPipelineTest {
    // This must be done in order to call opencv classes
    static {
        System.loadLibrary("opencv_java310");
    }

    /**
     * It should find a blue ball.  A jpeg image is read and fed into process method.
     */
    @Test
    public void itShouldFindABlueBallWithDarkerExposure() throws IOException {
        // Assemble
        Mat ballImage = Imgcodecs.imread((new File(".\\src\\test\\resource\\BlueRedBall-darker.jpg")).getCanonicalPath());
        BlueBallGripPipeline pipeline = new BlueBallGripPipeline();
        // Act
        pipeline.process(ballImage);
        // Assert
        assertEquals(1, pipeline.findBlobsOutput().total());
    }

    @Test
    public void itShouldFindABlueBallWithLighterExposure() throws IOException {
        // Assemble
        Mat ballImage = Imgcodecs.imread((new File(".\\src\\test\\resource\\BlueRedBall.jpg")).getCanonicalPath());
        BlueBallGripPipeline pipeline = new BlueBallGripPipeline();
        // Act
        pipeline.process(ballImage);
        // Assert
        assertEquals(1, pipeline.findBlobsOutput().total());
    }
}