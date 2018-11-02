import edu.wpi.cscore.*;
import org.junit.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;
import org.opencv.core.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.concurrent.*;

/**
 * Test that the ImagePump makes appropriate calls to dependent classes
 * passed into the constructor.  Note that there is a dependency on opencv and
 * changes required to launch.test.json.
 * 
 * @see <a href="https://www.chiefdelphi.com/forums/showthread.php?t=167097">Chief Delphi Article</a>
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class ImagePumpTest 
{
    // This must be done in order to call opencv classes
    static {
        System.loadLibrary("opencv_java310");
    }

    /**
     * Test that the ImagePump class grabs the next frame from an image sink
     * and returns a Future with a Mat. This test uses a trick to mock a reference
     * variable passed into a dependency method call (grabFrame in this case).
     * 
     * @see <a href="https://stackoverflow.com/questions/29643995/how-to-change-an-object-that-is-passed-by-reference-to-a-mock-in-mockito">Modify Reference Vars</a>
     * @author Chuck Benedict, Mentor, Team 997
     */
    @Test
    public void itPumpsTheImageSink() throws InterruptedException, ExecutionException {
        // Assemble

        // Create a fakey image that we will use to have the grabFrame function
        // of the mocked imageSink return. 
        Mat mockedImage = Mat.ones(3, 3, CvType.CV_8U);

        // Use Mockito to mock up our prerequisites
        // Use an Answer to intercept the action of filling the
        // supplied inputImage Mat of the grabFrame call...inside the pump method.
        CvSink imageSinkMock = mock(CvSink.class);
        doAnswer(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) {
                Mat mat = (Mat) invocation.getArguments()[0];
                mockedImage.copyTo(mat);
                return 1L;
            }
        }).when(imageSinkMock).grabFrame(any(Mat.class));

        // Wire up class under test
        ImagePump imagePump = new ImagePump(imageSinkMock);

        // Act
        Future<Mat> image = imagePump.pump();

        // Assert
        assertEquals(image.get().size(), mockedImage.size());
    }
}
