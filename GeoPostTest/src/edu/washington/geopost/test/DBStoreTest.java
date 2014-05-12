package edu.washington.geopost.test;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * DBStoreTest contains unit tests for the functionality of the DBStore class.
 * @author Andrew Repp
 *
 */

// TODO: This class will be hard to test. There are a couple things to think
// about.
// 
// First, we need to consider how to set up a user for testing. One thing to
// look into would be the ParseAnonymousUtils class, which has documentation at
// https://parse.com/docs/android/api/com/parse/ParseAnonymousUtils.html
// It might be able to help.
// 
// Second, we need to consider how to handle the fact that we're saving the
// results in the background. Handling threads is tricky. We might want to
// switch from using background threads to do the saving, just to make the
// testing easier. Unfortunately, this might have a significant effect on
// performance. Instead, we might want to look into a CountDownLatch, described
// at http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CountDownLatch.html

public class DBStoreTest {
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
