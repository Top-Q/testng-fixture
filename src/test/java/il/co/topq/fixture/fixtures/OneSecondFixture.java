package il.co.topq.fixture.fixtures;

import il.co.topq.fixture.Fixture;
import il.co.topq.fixture.FixtureException;

public class OneSecondFixture implements Fixture {

	@Override
	public Object setup(String... params) throws FixtureException {
		System.out.println("Starting....");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		if (params.length > 0) {
			System.out.println("Finished running fixture with parameter: " + params[0]);
		} else {
			System.out.println("Finished running fixture without parameters ");
		}
		return null;
	}

	@Override
	public void teardown() {
		System.out.println("Starting successful teardown");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
		System.out.println("ending successful teardown");
	}

	@Override
	public void failedTeardown() {
		System.out.println("In failed teardown");
	}
	
	

}
