package il.co.topq.fixture;

public interface Fixture {

	Object setup(String... params) throws FixtureException;
	
	void teardown();
	
	void failedTeardown();

}
