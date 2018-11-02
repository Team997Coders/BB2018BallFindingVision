import edu.wpi.first.wpilibj.networktables.*;

/**
 * Specific implementation for writing red ball data to network tables.
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class RedBallNetworkTableWriter extends NetworkTableWriter
{
    public RedBallNetworkTableWriter(BallPipelineInterpreter interpreter, NetworkTable publishingTable) {
        super(interpreter, publishingTable);
    }

    public String getBallFoundKey() {
        return "RedBallFound";
    }
    public String getBallCountKey() {
        return "RedBallCount";
    }
}