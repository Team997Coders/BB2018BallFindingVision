import java.io.*;
import static org.junit.Assert.assertEquals;

import org.junit.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Test the red ball pipeline to make sure it detects ball properly.
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class RedBallGripPipelineTest {
    // This must be done in order to call opencv classes
    static {
        System.loadLibrary("opencv_java310");
    }

    /**
     * It should find a red ball.  A jpeg image is read and fed into process method.
     */
    @Test
    public void itShouldFindARedBallWithDarkerExposure() throws IOException {
        // Assemble
        Mat ballImage = Imgcodecs.imread((new File(".\\src\\test\\resource\\BlueRedBall-darker.jpg")).getCanonicalPath());
        RedBallGripPipeline pipeline = new RedBallGripPipeline();
        // Act
        pipeline.process(ballImage);
        // Assert
        assertEquals(1, pipeline.findBlobsOutput().total());
    }

    @Test
    public void itShouldFindARedBallWithLighterExposure() throws IOException {
        // Assemble
        Mat ballImage = Imgcodecs.imread((new File(".\\src\\test\\resource\\BlueRedBall.jpg")).getCanonicalPath());
        RedBallGripPipeline pipeline = new RedBallGripPipeline();
        // Act
        pipeline.process(ballImage);
        // Assert
        assertEquals(1, pipeline.findBlobsOutput().total());
    }
}