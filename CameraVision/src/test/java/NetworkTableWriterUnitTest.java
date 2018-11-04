import edu.wpi.first.wpilibj.networktables.*;
import org.junit.*;
import static org.mockito.Mockito.*;

/**
 * Test that the NetworkTableWriter makes appropriate calls to dependent classes
 * passed into the constructor.  Note that a running version of network tables is
 * not required and in fact we don't care that is there.  We assume network tables
 * itself already works.
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class NetworkTableWriterUnitTest {
    /**
     * Test that the NetworkTableWriter class writes the ball found boolean
     * to a network table. Note the use of a helper concrete class to do this.
     * There are Mockito-isms one could do to pull this off, but this actually seemed easier.
     * 
     * @see <a href="https://www.baeldung.com/junit-test-abstract-class">Testing Abstract Classes</a>
     * 
     * @author Chuck Benedict, Mentor, Team 997
     */
    @Test
    public void itWritesBallFoundResult() {
        // Assemble

        boolean ballFound = true;
        // Use Mockito to mock up our prerequisites
        // Mock up our pipeline interpreter
        BallPipelineInterpreter interpreterMock = mock(BallPipelineInterpreter.class);
        when(interpreterMock.ballsFound()).thenReturn(ballFound);

        // Mock up a network table to write to
        NetworkTable networkTableMock = mock(NetworkTable.class);

        // Wire up class under test
        NetworkTableWriterConcrete networkTableWriter = new NetworkTableWriterConcrete(interpreterMock, networkTableMock);

        // Act
        networkTableWriter.write();

        // Assert
        verify(networkTableMock, times(1)).putBoolean(networkTableWriter.getBallFoundKey(), ballFound);
    }

    /**
     * Test that the NetworkTableWriter class writes the ball count 
     * to a network table. Note the use of a helper concrete class to do this.
     * There are Mockito-isms one could do to pull this off, but this actually seemed easier.
     */
    @Test
    public void itWritesBallCountResult() {
        // Assemble

        long ballCount = 10;
        // Use Mockito to mock up our prerequisites
        // Mock up our pipeline interpreter
        BallPipelineInterpreter interpreterMock = mock(BallPipelineInterpreter.class);
        when(interpreterMock.ballCount()).thenReturn(ballCount);

        // Mock up a network table to write to
        NetworkTable networkTableMock = mock(NetworkTable.class);

        // Wire up class under test
        NetworkTableWriterConcrete networkTableWriter = new NetworkTableWriterConcrete(interpreterMock, networkTableMock);

        // Act
        networkTableWriter.write();

        // Assert
        verify(networkTableMock, times(1)).putNumber(networkTableWriter.getBallCountKey(), ballCount);
    }
}