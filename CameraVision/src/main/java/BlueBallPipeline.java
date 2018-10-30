import org.opencv.core.*;

public class BlueBallPipeline implements IBallPipeline {
    private BlueBallGripPipeline pipeline = new BlueBallGripPipeline();

    public void process(Mat source0) {
        pipeline.process(source0);
    }
    public MatOfKeyPoint findBlobsOutput() {
        return pipeline.findBlobsOutput();
    }
    public String getColor() {
        return "Blue";
    }
}