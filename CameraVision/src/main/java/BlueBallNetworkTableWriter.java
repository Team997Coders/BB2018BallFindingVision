import edu.wpi.first.wpilibj.networktables.*;

/**
 * Specific implementation for writing blue ball data to network tables.
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class BlueBallNetworkTableWriter extends NetworkTableWriter
{
    public BlueBallNetworkTableWriter(BallPipelineInterpreter interpreter, NetworkTable publishingTable) {
        super(interpreter, publishingTable);
    }

    public String getBallFoundKey() {
        return "BlueBallFound";
    }
    public String getBallCountKey() {
        return "BlueBallCount";
    }
}