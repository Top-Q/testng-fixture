# testng-fixture

Time matters in test automation projects. In most cases we are eager to get the test result as fast as possible. The **testng-fixture** aims to help to achieve better execution times by allowing separation between the test and the test fixtures and running the test fixtures in parallel.
Test fixtures are all the necessary steps that are used for bringing the system under test to the state that is needed for us to run the test. 

To use the fixture mechanism, start by creating a new class that implements the **Fixture** inteface. 


## Features

* Allows creating test fixtures as separated classes
* Allows linking fixtures to tests in many to one relationships using annotations
* Allows pusblishing parameters from tests to fixtures using annotations
* Allows sending responses from fixtures to tests
* Running all test fixtures concurrently
* Running all teardowns at the end of runs, also concurrently
  

## Installation

### Maven

Since the project is not yet available in the central Maven repositories, you will need to add additional Maven repository in your project POM file

```` xml
<repositories>
	<repository>
		<id>topq</id>
		<url>http://maven.top-q.co.il/content/groups/public</url>
	</repository>
</repositories>
````

The artificat itself

```` XML
<dependency>
	<groupId>il.co.topq.testng</groupId>
	<artifactId>testng-fixture</artifactId>
	<version>1.0.0</version>
</dependency>
````

## Usage

First you will need to add a TestNG listener to your project. There are many ways to add listeners, but the simplest way to do it is using the `@Listeners` annotation

```Java
@Listeners(FixtureListener.class)
public abstract class AbstractTestCase {

}
```

Implement your fixture as class that implements the `Fixture` interface. Implement the `setup` method that will be called before your tests and the `teaddown` and `failedTeardown` methods that will be called after all the execution is done. The `failedTeaddown` will be called only if the `setup` method failed.


```java
public class FixtureExample implements Fixture {

	@Override
	public Object setup(String... params) throws FixtureException {
		if (params.length == 0) {
			System.out.println("In setup phase with params: " + Arrays.toString(params));
		} else{
			System.out.println("In setup phase");
		}
		
		return "Message from fixture";
	}

	@Override
	public void teardown() {
		System.out.println("Teardown after successful setup");
	}

	@Override
	public void failedTeardown() {
		System.out.println("Teardown after failed setup");
	}

}
```

To use the Fixture in your test, add the **WithFixture** annotation and add the class of the fixture you want to use

```java
	@Test
	@WithFixture(il.co.topq.fixture.fixtures.FixtureExample.class)
	public void myAwesomeTest() {
		// This is my test
	}

```
if you want to pass parameters to you fixture, add them as array of strings in the same fixture

```Java
	@Test
	@WithFixture(value = il.co.topq.fixture.fixtures.FixtureExample.class, params = {"firstValue","secondValue"})
	public void myAwesomeTest() {
		// This is my test
	}

```

Now, in your `@BeforeMethod` method, you will need to add the following code for getting the fixture running. this can be in the same class that you are using using to attach the `FixtureListener` class as TestNG listener

```Java
@Listeners(FixtureListener.class)
public abstract class AbstractTestCase {

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
```

Now, all the fixtures will be executed before the first test is run in parallel. At the end of the run, all the fixtures teardowns will be executed. If the setup was successfull the `testdown` methodd will be called and if the setup phase failed, then the `failedTeardown` methods will be the one to get executed.

**Important Notice**: If you define the same fixture with the same parameters in multiple tests, the fixture will be executed only once.

**Important Limitation:** The fixtures will not work on tests that are part of a dependency chain since TestNG will not expose them to the methods list available for the `IMethodInterceptor` that is used to get the list of all discovered test methods.
