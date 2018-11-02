/**
* Interpret the result of the ball pipeline.  This abstracts out the logic
* from the pipeline class.
*
* @author Chuck Benedict, Mentor, Team 997
*/
public class BallPipelineInterpreter {

	// Processed pipeline that we will do the interpretation against
	private IBallPipeline pipeline;

	/**
	* Constructor taking a processed pipeline
	*
	* @param pipeline	A processed pipeline that returns blob found results
	*/
	public BallPipelineInterpreter(IBallPipeline pipeline) {
		if (pipeline == null)
		{
			throw new IllegalArgumentException("Pipline cannot be null.");
		}
		this.pipeline = pipeline;
	}

	/**
	 * Did we find at least one ball on a processed frame?
	 * 
	 * @return True if at least one ball was found
	 */
	public boolean ballsFound() {
		return !this.pipeline.findBlobsOutput().empty();
	}

	/**
	 * Get the count of the number of balls found on a processed frame.
	 *  
	 * @return The count of the number of balls found
	 */
	public long ballCount() {
		return this.pipeline.findBlobsOutput().total();
	}
}