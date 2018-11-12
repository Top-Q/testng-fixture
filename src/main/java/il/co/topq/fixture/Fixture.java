package il.co.topq.fixture;

public interface Fixture {

	public void perform(String... params) throws FixtureException;

}
