package edu.washington.geopost.test;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * DBQueryTest contains unit tests for the functionality of the DBQuery class.
 * @author Andrew Repp
 *
 */

// TODO: This class will be hard to test.
// We need to consider how to set up a user for testing. One thing to
// look into would be the ParseAnonymousUtils class, which has documentation at
// https://parse.com/docs/android/api/com/parse/ParseAnonymousUtils.html
// It might be able to help.

public class DBQueryTest {
	@BeforeClass
	public static void oneTimeSetUp() {
		
	}
	
	@AfterClass
	public static void oneTimeTearDown() {
		
	}
	
	@Test
	public void testExample() {
		assertTrue(true);
	}
}
