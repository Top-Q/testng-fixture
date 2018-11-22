package il.co.topq.fixture;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import il.co.topq.fixture.FixtureManager;
import il.co.topq.fixture.FixtureMethodInterceptor;
import il.co.topq.fixture.FixtureManager.FixtureRunResult;

@Listeners(FixtureMethodInterceptor.class)
public class AbstractTestCase {

	protected Object fixtureResult;
	
	@BeforeMethod
	public void setup(Method method) throws Exception {
		FixtureManager.getInstance().startFixtureSetupRuns();
		FixtureManager.getInstance().waitForAllFixtureSetupRunsToEnd();
		if (!FixtureManager.getInstance().isMethodHasFixture(method)) {
			return;
		}
		FixtureRunResult runResult = FixtureManager.getInstance().getFixtureRunResult(method);
		if (!runResult.isStatus()) {
			throw new Exception("Test failed in fixture phase", runResult.getThrowable());
		}
		fixtureResult = runResult.getResult();
		
	}

}
