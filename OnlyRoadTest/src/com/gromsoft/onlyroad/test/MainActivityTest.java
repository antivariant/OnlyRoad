package com.gromsoft.onlyroad.test;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;




public class MainActivityTest extends TestCase {

	public MainActivityTest(String name) {
		super(name);
	}

	public MainActivityTest(){
		this("My First Project Test");
	}
	
	protected void setUp() throws Exception {
		super.setUp();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testSomething(){
		fail("Not implemented yet");
	}
	
}
