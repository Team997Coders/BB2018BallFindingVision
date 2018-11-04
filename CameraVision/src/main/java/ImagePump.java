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
    private Future<Mat> pumpAsyncFuture;

    /**
     * This class wraps up the pumping of images from an image sink.
     * 
     * @param imageSink This is the sink that will be pumped for the next frame
     */
    public ImagePump(CvSink imageSink) {
        if (imageSink == null) {
            throw new IllegalArgumentException("imageSink cannot be null");
        }
        this.imageSink = imageSink;
        this.inputImage = new Mat();
        this.pumpAsyncFuture = null;
    }

    /**
     * Pump the image sink for the next frame asynchronously.
     */
    public void pumpAsync() {
        if (pumpAsyncFuture != null) {
            throw new IllegalAccessError("Only one pump can be awaited at a time.");
        }
        pumpAsyncFuture = executor.submit(() -> {
            return pumpInternal();
        });
    }

    /**
     * Pump the image sink for the next frame.
     */
    public Mat pump() {
        return pumpInternal();
    }

    /**
     * DRY up pumping.
     */
    private Mat pumpInternal() {
        long frameTime;
        frameTime = imageSink.grabFrame(inputImage);
        if (frameTime == 0) {
            System.out.println(imageSink.getError());
            return new Mat();
        }
        return inputImage.clone();
    }

    /**
     * Awaits completion of pumpAsync call.
     * 
     * @return  The next image pumped from the sink
     */
    public Mat awaitPumpCompletion() {
        Mat pumpedImage = null;
        try {
            pumpedImage = pumpAsyncFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {
            throw new IllegalAccessError("You must call pumpAsync first before awaiting completion.");
        } finally {
            // Reset our future
            pumpAsyncFuture = null;
            if (pumpedImage == null) {
                pumpedImage = new Mat();
            }
        }
        return pumpedImage;
    }
}