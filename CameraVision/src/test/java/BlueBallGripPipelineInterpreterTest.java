import static org.junit.Assert.*;
import org.junit.*;
import org.opencv.core.MatOfKeyPoint;

import static org.mockito.Mockito.*;

/**
 * Unit tests for blue ball vision pipeline interpreter class
 * We factor out dependencies of having to have an executed pipeline using Mockito.
 */
public class BlueBallGripPipelineInterpreterTest 
{
    @Test
    public void itShouldFindBalls()
    {
        // use Mockito to mock a pipeline instance
        // we don't want to have to wire up a real pipeline object to unit test the interperter class
        BlueBallGripPipeline pipeline = mock(BlueBallGripPipeline.class);

        // Wire up our mocked method to return what we need to test the interpreter class
        // Note that we mock a returned instance of findBlobsOutput (MatOfKeyPoint in this case), 
        // and then mock the needed method on that class.
        when(pipeline.findBlobsOutput()).thenReturn(mock(MatOfKeyPoint.class));
        when(pipeline.findBlobsOutput().empty()).thenReturn(false);

        // instantiate our class to test
        BlueBallGripPipelineInterpreter interpreter = new BlueBallGripPipelineInterpreter(pipeline);

        // We should find balls
        assertTrue(interpreter.ballsFound());
    }

    @Test
    public void itShouldCountBalls()
    {
        //TODO: DRY this instantiation ceremony up across tests!
        // use Mockito to mock a pipeline instance
        // we don't want to have to wire up a real pipeline object to unit test the interperter class
        BlueBallGripPipeline pipeline = mock(BlueBallGripPipeline.class);
        long countedBalls = 10;

        // Wire up our mocked method to return what we need to test the interpreter class
        // Note that we mock a returned instance of findBlobsOutput (MatOfKeyPoint in this case), 
        // and then mock the needed method on that class.
        when(pipeline.findBlobsOutput()).thenReturn(mock(MatOfKeyPoint.class));
        when(pipeline.findBlobsOutput().total()).thenReturn(countedBalls);

        // instantiate our class to test
        BlueBallGripPipelineInterpreter interpreter = new BlueBallGripPipelineInterpreter(pipeline);

        // We should get total count of balls found
        assertEquals(countedBalls, interpreter.ballCount());
    }
}
