import edu.wpi.cscore.*;
import org.opencv.core.*;
import java.util.concurrent.*;

public class ImagePump {
    private CvSink imageSink;
    private Mat inputImage;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ImagePump(CvSink imageSink) {
        this.imageSink = imageSink;
        this.inputImage = new Mat();
    }

    Future<Mat> pump() {
        return executor.submit(() -> {
            long frameTime;
            frameTime = imageSink.grabFrame(inputImage);
            if (frameTime == 0) {
                System.out.println(imageSink.getError());
                return new Mat();
            }
            return inputImage.clone();
        });
    }
}