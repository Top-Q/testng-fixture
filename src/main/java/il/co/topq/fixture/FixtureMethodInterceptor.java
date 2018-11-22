package il.co.topq.fixture;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;

/**
 * Responsible for getting the list of test methods that were discovered and
 * publish them to the Fixture manager
 * 
 * @author Itai Agmon
 *
 */
public class FixtureMethodInterceptor implements IMethodInterceptor, ISuiteListener {

	/**
	 * Get the list of method from TestNG and publish them to the Fixture Manager.
	 */
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
//		@formatter:off
		final List<Method> testMethodsWithFixture = methods.stream()
				.map(m -> m.getMethod().getConstructorOrMethod())
				.filter(m -> m.getMethod().isAnnotationPresent(WithFixture.class))
				.map(m -> m.getMethod())
				.collect(Collectors.toList());
//		@formatter:on
		FixtureManager.getInstance().addMethods(testMethodsWithFixture);
		return methods;

	}

	@Override
	public void onFinish(ISuite suite) {
		FixtureManager.getInstance().startFixtureTeardownRun();
	}

	@Override
	public void onStart(ISuite suite) {
	}


}
