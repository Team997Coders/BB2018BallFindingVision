import org.junit.*;
import org.opencv.core.*;
import static org.mockito.Mockito.*;

/**
 * Test that the ImageProcessor makes appropriate calls to dependent classes
 * passed into the constructor.  Note that there is a dependency on opencv and
 * changes required to launch.test.json.
 * 
 * @see <a href="https://www.chiefdelphi.com/forums/showthread.php?t=167097">Chief Delphi Article</a>
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class ImageProcessorUnitTest 
{
    // This must be done in order to call opencv classes
    static {
        System.loadLibrary("opencv_java310");
    }

    /**
     * Test that the ImageProcessor calls the process method, asynchronously,
     * of the passed in pipeline. For this test, we do not care what the pipeline
     * itself actually does.
     */
    @Test
    public void itShouldProcessMyPipeline() {
        // Assemble
        // use Mockito to mock a pipeline and network table writer object
        IBallPipeline pipelineMock = mock(IBallPipeline.class);
        INetworkTableWriter networkTableWriterMock = mock(INetworkTableWriter.class);
        ImageProcessor imageProcessor = new ImageProcessor(pipelineMock, networkTableWriterMock);
        Mat emptyImage = new Mat();

        // Act
        imageProcessor.processAsync(emptyImage);
        imageProcessor.awaitProcessCompletion();

        //Assert
        verify(pipelineMock, times(1)).process(emptyImage);
    }

    /**
     * Test that the ImageProcessor calls the write method, asynchronously,
     * of the passed in networkTablesWriter. For this test, we do not care what the write
     * itself actually does.
     */
    @Test
    public void itShouldWriteToNetworkTables() {
        // Assemble
        // use Mockito to mock a pipeline and network table writer object
        IBallPipeline pipelineMock = mock(IBallPipeline.class);
        INetworkTableWriter networkTableWriterMock = mock(INetworkTableWriter.class);
        ImageProcessor imageProcessor = new ImageProcessor(pipelineMock, networkTableWriterMock);
        Mat emptyImage = new Mat();

        // Act
        imageProcessor.processAsync(emptyImage);
        imageProcessor.awaitProcessCompletion();

        //Assert
        verify(networkTableWriterMock, times(1)).write();
    }
}