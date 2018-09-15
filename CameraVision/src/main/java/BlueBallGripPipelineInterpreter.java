/*
* BlueBallGripPipelineInterpreter class.
*
* <p>Interpret the result of the blue ball pipeline.  This abstracts out the logic
*    from the pipeline class
*
* @author Chuck Benedict
*/
public class BlueBallGripPipelineInterpreter {

	// Private class vars
	private BlueBallGripPipeline pipeline;

	// Constructor taking a processed pipeline
	public BlueBallGripPipelineInterpreter(BlueBallGripPipeline pipeline) {
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