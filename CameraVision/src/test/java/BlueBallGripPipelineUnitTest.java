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
public class BlueBallGripPipelineUnitTest {
    // This must be done in order to call opencv classes
    static {
        System.loadLibrary("opencv_java310");
    }

    //TODO: Images with multiple balls?
    //TODO: Images with proven false positives?

    /**
     * It should find a blue ball.
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

    /**
     * It should still find a blue ball.
     */
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