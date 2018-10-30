import org.opencv.core.*;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import java.util.concurrent.*;

public class ImageProcessor {
    private IBallPipeline pipeline;
    private INetworkTableWriter networkTableWriter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Mat outputImage = new Mat();

    public ImageProcessor(IBallPipeline pipeline, INetworkTableWriter networkTableWriter) {
        if (pipeline == null) {
            throw new IllegalArgumentException();
        }
        this.pipeline = pipeline;
        this.networkTableWriter = networkTableWriter;
    }

    public Future<Void> process(Mat inputImage)
    {
        return executor.submit(() -> {
            // Apply the pipeline to the image.
            pipeline.process(inputImage);

            // Update network table
            networkTableWriter.write();
            return null;
        });
    }

    public Mat annotate(Mat inputImage) {
        // Write a processed image that you want to restream
        // This is a marked up image of what the camera sees
        Features2d.drawKeypoints(
            inputImage, 
            pipeline.findBlobsOutput(), 
            outputImage, 
            new Scalar(2,254,255),              // yellowish circle 
            Features2d.DRAW_RICH_KEYPOINTS);    // draws a full-sized circle around found point(s)
        
        // Ident balls on image
        for (KeyPoint k : pipeline.findBlobsOutput().toArray()) {
            Imgproc.putText(outputImage, pipeline.getColor(), k.pt, Core.FONT_HERSHEY_COMPLEX_SMALL, .75, new Scalar(2,254,255));
        }
        return outputImage;
    }
}