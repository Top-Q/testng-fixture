package il.co.topq.fixture.fixtures;

import il.co.topq.fixture.Fixture;
import il.co.topq.fixture.FixtureException;

public class FaliedFixture implements Fixture {

	@Override
	public Object setup(String... params) throws FixtureException {
		System.out.println("About to fail fixture");
		throw new FixtureException("Oh no!!!!!  Setup was failed ");
	}

	@Override
	public void teardown() {
		System.out.println("In successful teardown");
	}

	@Override
	public void failedTeardown() {
		System.out.println("In failed teardown");
	}


}
