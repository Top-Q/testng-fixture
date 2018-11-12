package il.co.topq.fixture;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import il.co.topq.fixture.FixtureManager;
import il.co.topq.fixture.FixtureMethodInterceptor;
import il.co.topq.fixture.FixtureManager.FixtureRunResult;

@Listeners(FixtureMethodInterceptor.class)
public class AbstractTestCase {

	@BeforeMethod
	public void setup(Method method) throws Exception {
		FixtureManager.getInstance().startFixtureRuns();
		FixtureManager.getInstance().waitForAllFixtureRunsToEnd();
		if (!FixtureManager.getInstance().isMethodHasFixture(method)) {
			return;
		}
		FixtureRunResult result = FixtureManager.getInstance().getFixtureRunResult(method);
		if (!result.isStatus()) {
			throw new Exception("Test failed in fixture phase", result.getThrowable());
		}
		
	}

}
