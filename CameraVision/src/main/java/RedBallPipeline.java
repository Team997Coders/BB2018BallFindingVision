import org.opencv.core.*;

public class RedBallPipeline implements IBallPipeline {
    private RedBallGripPipeline pipeline = new RedBallGripPipeline();

    public void process(Mat source0) {
        pipeline.process(source0);
    }
    public MatOfKeyPoint findBlobsOutput() {
        return pipeline.findBlobsOutput();
    }
    public String getColor() {
        return "Red";
    }
}