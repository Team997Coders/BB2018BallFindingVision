import edu.wpi.first.wpilibj.networktables.*;

public class BlueBallNetworkTableWriter implements INetworkTableWriter
{
    BallPipelineInterpreter interpreter;
    NetworkTable publishingTable;

    public BlueBallNetworkTableWriter(BallPipelineInterpreter interpreter, NetworkTable publishingTable) {
        this.interpreter = interpreter;
        this.publishingTable = publishingTable;
    }

    public void write() {
        publishingTable.putBoolean("BlueBallFound", interpreter.ballsFound());
        publishingTable.putNumber("BlueBallCount", interpreter.ballCount());
    }
}