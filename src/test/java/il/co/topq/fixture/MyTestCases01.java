package il.co.topq.fixture;

import org.testng.annotations.Test;

import il.co.topq.fixture.WithFixture;
import il.co.topq.fixture.fixtures.OneSecondFixture;

public class MyTestCases01 extends AbstractTestCase {

	@Test
	@WithFixture(value = il.co.topq.fixture.fixtures.OneSecondFixture.class, params = { "1.7" })
	public void test01_01() {

	}

	@Test
	@WithFixture(value = il.co.topq.fixture.fixtures.OneSecondFixture.class, params = { "1.7" })
	public void test01_02() {

	}

	@Test
	@WithFixture(value = OneSecondFixture.class, params = { "1.8" })
	public void test01_03() {

	}

}
