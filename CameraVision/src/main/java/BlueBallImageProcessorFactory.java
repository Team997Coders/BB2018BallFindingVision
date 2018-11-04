import edu.wpi.first.wpilibj.networktables.*;

/**
 * Factory class to wire up a blue ball image processor
 * with all the correct dependencies.
 */
public class BlueBallImageProcessorFactory {
  /**
   * Static helper to create an image processor instance.
   * @param networkTable  The network table to write to
   * @return
   */
    public static ImageProcessor CreateImageProcessor(NetworkTable networkTable) {
        IBallPipeline blueBallPipeline = new BlueBallPipeline();
        return 
          new ImageProcessor(
            blueBallPipeline, 
            new BlueBallNetworkTableWriter(
              new BallPipelineInterpreter(blueBallPipeline), 
              networkTable));
    }
}