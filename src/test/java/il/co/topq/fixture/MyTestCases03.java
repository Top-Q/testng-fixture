package il.co.topq.fixture;

import org.testng.annotations.Test;

import il.co.topq.fixture.WithFixture;

public class MyTestCases03  extends AbstractTestCase {
	
	@Test
	public void test03_01() {
		
	}
	
	@Test
	@WithFixture(OneSecondFixture.class)
	public void test03_02() {
		
	}
	
	@Test
	public void test03_03() {
		
	}
	
}
