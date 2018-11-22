package il.co.topq.fixture;

import org.testng.annotations.Test;

import il.co.topq.fixture.WithFixture;
import il.co.topq.fixture.fixtures.FaliedFixture;
import il.co.topq.fixture.fixtures.OneSecondFixture;

public class MyTestCases02 extends AbstractTestCase {

	@Test
	public void test02_01() {

	}

	@Test
	@WithFixture(OneSecondFixture.class)
	public void test02_02() {

	}

	@Test
	@WithFixture(FaliedFixture.class)
	public void test02_03() {

	}

}
