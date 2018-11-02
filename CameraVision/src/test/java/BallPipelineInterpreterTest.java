import static org.junit.Assert.*;
import org.junit.*;
import org.opencv.core.MatOfKeyPoint;

import static org.mockito.Mockito.*;

/**
 * Unit tests for ball vision pipeline interpreter class
 * We factor out dependencies of having to have an executed pipeline using Mockito.
 */
public class BallPipelineInterpreterTest 
{
    /**
     * Test that the interpreter class finds balls.
     */
    @Test
    public void itShouldFindBalls()
    {
        // use Mockito to mock a pipeline instance
        // we don't want to have to wire up a real pipeline object to unit test the interperter class
        IBallPipeline pipeline = mock(IBallPipeline.class);

        // Wire up our mocked method to return what we need to test the interpreter class
        // Note that we mock a returned instance of findBlobsOutput (MatOfKeyPoint in this case), 
        // and then mock the needed method on that class.
        when(pipeline.findBlobsOutput()).thenReturn(mock(MatOfKeyPoint.class));
        when(pipeline.findBlobsOutput().empty()).thenReturn(false);

        // instantiate our class to test
        BallPipelineInterpreter interpreter = new BallPipelineInterpreter(pipeline);

        // We should find balls
        assertTrue(interpreter.ballsFound());
    }

    /**
     * Test that the interpreter class counts balls.
     */
    @Test
    public void itShouldCountBalls()
    {
        // use Mockito to mock a pipeline instance
        // we don't want to have to wire up a real pipeline object to unit test the interperter class
        IBallPipeline pipeline = mock(IBallPipeline.class);
        long countedBalls = 10;

        // Wire up our mocked method to return what we need to test the interpreter class
        // Note that we mock a returned instance of findBlobsOutput (MatOfKeyPoint in this case), 
        // and then mock the needed method on that class.
        when(pipeline.findBlobsOutput()).thenReturn(mock(MatOfKeyPoint.class));
        when(pipeline.findBlobsOutput().total()).thenReturn(countedBalls);

        // instantiate our class to test
        BallPipelineInterpreter interpreter = new BallPipelineInterpreter(pipeline);

        // We should get total count of balls found
        assertEquals(countedBalls, interpreter.ballCount());
    }
}
