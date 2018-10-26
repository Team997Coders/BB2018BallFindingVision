/*
* redBallGripPipelineInterpreter class.
*
* <p>Interpret the result of the red ball pipeline.  This abstracts out the logic
*    from the pipeline class
*
* @author Chuck Benedict
*/
public class RedBallGripPipelineInterpreter {

	// Private class vars
	private RedBallGripPipeline pipeline;

	// Constructor taking a processed pipeline
	public RedBallGripPipelineInterpreter(RedBallGripPipeline pipeline) {
		if (pipeline == null)
		{
			throw new IllegalArgumentException("Pipline cannot be null.");
		}
		this.pipeline = pipeline;
	}

	public boolean ballsFound() {
		return !this.pipeline.findBlobsOutput().empty();
	}

	public long ballCount() {
		return this.pipeline.findBlobsOutput().total();
	}
}