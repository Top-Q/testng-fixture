package il.co.topq.fixture;

import il.co.topq.fixture.Fixture;
import il.co.topq.fixture.FixtureException;

public class OneSecondFixture implements Fixture {

	@Override
	public void perform(String... params) throws FixtureException {
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
	}

}
