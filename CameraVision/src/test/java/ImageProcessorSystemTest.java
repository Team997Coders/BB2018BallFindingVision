import edu.wpi.first.wpilibj.networktables.*;

import static org.junit.Assert.assertEquals;

import java.io.*;
import org.junit.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * Because we have a network tables simulator as part of this project,
 * we can start it up and test the image processor end-to-end.
 */
public class ImageProcessorSystemTest {
    private static Process ntserverProc;

    @BeforeClass
    public static void startNTServer() throws IOException {
        // Command to run network tables server 
        String command = (new File("..//bin//python//scripts//ntserver.exe")).getCanonicalPath();

        // Running the above command 
        ntserverProc = Runtime.getRuntime().exec(command); 
    }

    @AfterClass
    public static void killNTServer() {
        if (ntserverProc != null) {
            ntserverProc.destroy();
        }
    }

    @Test
    public void itWritesBlueBallResultsFoundToNetworkTable() throws IOException, InterruptedException {
        // Assemble
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("localhost");
        NetworkTable.initialize();
        NetworkTable networkTable = NetworkTable.getTable("SmartDashboard");
      
        ImageProcessor blueBallImageProcessor = BlueBallImageProcessorFactory.CreateImageProcessor(networkTable);
        // TODO: This should be a helper since it is repeated
        Mat inputImage = Imgcodecs.imread((new File(".\\src\\test\\resource\\BlueRedBall-darker.jpg")).getCanonicalPath());

        // Assemble utility components that will be needed to compare results to 
        BlueBallPipeline blueBallPipeline = new BlueBallPipeline();
        blueBallPipeline.process(inputImage);
        BallPipelineInterpreter ballPipelineInterpreter = new BallPipelineInterpreter(blueBallPipeline);
        BlueBallNetworkTableWriter blueBallNetworkTableWriter = 
            new BlueBallNetworkTableWriter(
                ballPipelineInterpreter, 
                networkTable);

        // Act
        blueBallImageProcessor.processAsync(inputImage);
        blueBallImageProcessor.awaitProcessCompletion();

        //Assert
        // Sleep for at least 10ms because network tables has a 10hz writing heartbeat
        Thread.sleep(20);
        assertEquals(ballPipelineInterpreter.ballsFound(), networkTable.getBoolean(blueBallNetworkTableWriter.getBallFoundKey()));
        assertEquals(ballPipelineInterpreter.ballCount(), (long)networkTable.getNumber(blueBallNetworkTableWriter.getBallCountKey()));
    }
}