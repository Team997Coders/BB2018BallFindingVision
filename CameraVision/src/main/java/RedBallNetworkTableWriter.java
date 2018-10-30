import edu.wpi.first.wpilibj.networktables.*;

public class RedBallNetworkTableWriter implements INetworkTableWriter
{
    BallPipelineInterpreter interpreter;
    NetworkTable publishingTable;

    public RedBallNetworkTableWriter(BallPipelineInterpreter interpreter, NetworkTable publishingTable) {
        this.interpreter = interpreter;
        this.publishingTable = publishingTable;
    }

    public void write() {
        publishingTable.putBoolean("RedBallFound", interpreter.ballsFound());
        publishingTable.putNumber("RedBallCount", interpreter.ballCount());
    }
}