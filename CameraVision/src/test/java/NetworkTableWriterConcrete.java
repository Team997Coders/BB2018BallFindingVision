import edu.wpi.first.wpilibj.networktables.*;

/**
 * Concerete implementation of network table writer for unit tests.
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class NetworkTableWriterConcrete extends NetworkTableWriter
{
    public NetworkTableWriterConcrete(BallPipelineInterpreter interpreter, NetworkTable publishingTable) {
        super(interpreter, publishingTable);
    }

    public String getBallFoundKey() {
        return "BallFound";
    }
    public String getBallCountKey() {
        return "BallCount";
    }
}