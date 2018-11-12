package il.co.topq.fixture;

/**
 * Exception for describing that something went bad while executing fixture.
 * 
 * @author Itai Agmon
 *
 */
public class FixtureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FixtureException(String message) {
		super(message);
	}

	public FixtureException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
