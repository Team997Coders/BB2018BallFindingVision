import edu.wpi.first.wpilibj.networktables.*;
import java.io.*;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageProcessorIntegrationTest {
    /**
     * Checks that when a blue ball pipeline is processed with an actual image
     * that the correct results are attempted to be written to a network table.
     */
    @Test
    public void itWritesBlueBallResultsFoundToNetworkTable() throws IOException {
        // Assemble
        // Mock a network table to test that we wrote the correct result to it
        NetworkTable networkTableMock = mock(NetworkTable.class);
        // Object under test
        ImageProcessor blueBallImageProcessor = 
            BlueBallImageProcessorFactory.CreateImageProcessor(networkTableMock);
        
            // TODO: This should be a helper since it is repeated
        Mat inputImage = 
            Imgcodecs.imread((new File(".\\src\\test\\resource\\BlueRedBall-darker.jpg")).getCanonicalPath());

        // Assemble utility components that will be needed to compare results 
        BlueBallPipeline blueBallPipeline = new BlueBallPipeline();
        blueBallPipeline.process(inputImage);
        BlueBallNetworkTableWriter blueBallNetworkTableWriter = 
            new BlueBallNetworkTableWriter(
                new BallPipelineInterpreter(blueBallPipeline), 
                networkTableMock);

        // Act
        blueBallImageProcessor.processAsync(inputImage);
        blueBallImageProcessor.awaitProcessCompletion();

        //Assert
        verify(networkTableMock, times(1)).putBoolean(
            blueBallNetworkTableWriter.getBallFoundKey(), 
            !blueBallPipeline.findBlobsOutput().empty()
        );
        verify(networkTableMock, times(1)).putNumber(
            blueBallNetworkTableWriter.getBallCountKey(), 
            blueBallPipeline.findBlobsOutput().total()
        );
    }
    /**
     * Checks that when a red ball pipeline is processed with an actual image
     * that the correct results are attempted to be written to a network table.
     */
    @Test
    public void itWritesRedBallResultsFoundToNetworkTable() throws IOException {
        // Assemble
        // Mock a network table to test that we wrote the correct result to it
        NetworkTable networkTableMock = mock(NetworkTable.class);
        ImageProcessor redBallImageProcessor = RedBallImageProcessorFactory.CreateImageProcessor(networkTableMock);
        // TODO: This should be a helper since it is repeated
        Mat inputImage = Imgcodecs.imread((new File(".\\src\\test\\resource\\BlueRedBall-darker.jpg")).getCanonicalPath());

        // Assemble utility components that will be needed to compare results to 
        RedBallPipeline redBallPipeline = new RedBallPipeline();
        redBallPipeline.process(inputImage);
        RedBallNetworkTableWriter redBallNetworkTableWriter = 
            new RedBallNetworkTableWriter(
                new BallPipelineInterpreter(redBallPipeline), 
                networkTableMock);

        // Act
        redBallImageProcessor.processAsync(inputImage);
        redBallImageProcessor.awaitProcessCompletion();

        //Assert
        verify(networkTableMock, times(1)).putBoolean(
            redBallNetworkTableWriter.getBallFoundKey(), 
            !redBallPipeline.findBlobsOutput().empty()
        );
        verify(networkTableMock, times(1)).putNumber(
            redBallNetworkTableWriter.getBallCountKey(), 
            redBallPipeline.findBlobsOutput().total()
        );
    }
}