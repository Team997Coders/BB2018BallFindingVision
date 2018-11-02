import edu.wpi.cscore.*;
import org.opencv.core.*;
import java.util.concurrent.*;

/**
 * This class sets up the use of futures to read the next frame
 * from an opencv imageSink.
 * 
 * @author Chuck Benedict, Mentor, Team 997
 */
public class ImagePump {
    private CvSink imageSink;
    private Mat inputImage;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 
     * @param imageSink This is the sink that will be pumped for the next frame
     */
    public ImagePump(CvSink imageSink) {
        if (imageSink == null) {
            throw new IllegalArgumentException("imageSink cannot be null");
        }
        this.imageSink = imageSink;
        this.inputImage = new Mat();
    }

    /**
     * Pump the image sink for the next frame.
     * 
     * @return A Future returning a Mat containing the next frame
     */
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