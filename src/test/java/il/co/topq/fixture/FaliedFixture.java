package il.co.topq.fixture;

import il.co.topq.fixture.Fixture;
import il.co.topq.fixture.FixtureException;

public class FaliedFixture implements Fixture {

	@Override
	public void perform(String... params) throws FixtureException {
		System.out.println("About to fail fixture");
		throw new FixtureException("Oh no!!!!!  Setup was failed ");
	}

}
