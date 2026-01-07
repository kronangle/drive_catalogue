package com.clearanglestudios.drive_catalogue;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * [Week 1] Test Environment Configuration Purpose: Verify that JUnit is
 * correctly installed and running.
 */
public class EnvironmentTest {

	@Test
	public void simpleMathTest() {
		int a = 1;
		int b = 1;
		assertTrue("Math should work", a + b == 2);
	}

	@Test
	public void checkTestAccessToApp() {
		// Verify we can access the App class
		boolean isDev = App.isTestEnvironment;
		assertTrue(true);
	}
}