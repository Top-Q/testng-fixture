package il.co.topq.fixture;

import org.testng.annotations.Test;

import il.co.topq.fixture.WithFixture;
import il.co.topq.fixture.fixtures.FaliedFixture;
import il.co.topq.fixture.fixtures.OneSecondFixture;
import il.co.topq.fixture.fixtures.FixtureWithResult;
import il.co.topq.fixture.fixtures.MyFixtureResult;

public class TestsWithFixtureExamples extends AbstractTestCase {

	/**
	 * Can attach fixture to test. If no parameters are used, there is no need to
	 * add the fixture class with value property.
	 */
	@Test
	@WithFixture(OneSecondFixture.class)
	public void test01_01() {

	}

	/**
	 * Can add parameters to fixtures
	 */
	@Test
	@WithFixture(value = OneSecondFixture.class, params = { "1.8" })
	public void test01_02() {

	}

	/**
	 * Can add multiple parameters to fixtures
	 */
	@Test
	@WithFixture(value = OneSecondFixture.class, params = { "1.7", "1.8" })
	public void test01_03() {

	}

	/**
	 * Has the same fixture as test01_03 so the fixture will not run again
	 */
	@Test
	@WithFixture(FaliedFixture.class)
	public void test01_04() {

	}
	
	@Test
	@WithFixture(FixtureWithResult.class)
	public void testFixtureWithResult() {
		System.out.println(((MyFixtureResult)fixtureResult).getMessage());
	}

}
