package edu.washington.geopost.test;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.android.gms.maps.model.LatLng;

import edu.washington.geopost.DBStore;
import edu.washington.geopost.Pin;

/**
 * 
 * DBStoreTest contains unit tests for the functionality of the DBStore class.
 * @author Andrew Repp, Neil Hinnant
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
	
	private static DBStore pinWriter;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		pinWriter = new DBStore ();
	}
	
	@AfterClass
	public static void oneTimeTearDown() {
		
	}
	
	//Test a single pin drop
	@Test
	public void testSinglePin() {
		String message = "This is a test pin";
		LatLng coord = new LatLng(35.445, 47.555);
		Pin pin = pinWriter.postPin(coord, message);
		
		assertTrue(message.equals(pin.getMessage()));
		assertTrue(coord.equals(pin.getCoord()));
	}
	
	//Test a multiple pin drop
	@Test
	public void testMultiPin() {
		LatLng coord1 = new LatLng(25.225, 56.553);
		LatLng coord2 = new LatLng(23.455, 57.898);
		LatLng coord3 = new LatLng(26.999, 58.550);
		
		Pin pin1 = pinWriter.postPin(coord1, "Pin1");
		Pin pin2 = pinWriter.postPin(coord2, "Pin2");
		Pin pin3 = pinWriter.postPin(coord3, "Pin3");
		
		assertTrue("Pin1".equals(pin1.getMessage()));
		assertTrue(coord1.equals(pin1.getCoord()));
		
		assertTrue("Pin2".equals(pin2.getMessage()));
		assertTrue(coord2.equals(pin2.getCoord()));
		
		assertTrue("Pin3".equals(pin3.getMessage()));
		assertTrue(coord3.equals(pin3.getCoord()));
	}
	
	//TODO
	//Test a duplicate location pin drop (with different messages)
	//What is the behavior here? I'm not sure we've defined it. 
	//I seem to recall we wanted dupes to overwrite.
	@Test
	public void testDuplicatePin() {
		LatLng coord = new LatLng(47.325, -18.25);
		Pin first = pinWriter.postPin(coord, "First Message");
		Pin second = pinWriter.postPin(coord, "Second Message");
	}
	
	//Test a null pin drop
	@Test 
	public void testNullPin() {
		LatLng nullCoord = null;
		Pin nullPin = pinWriter.postPin(nullCoord, "Sooooo null");
		
		assertTrue(nullPin == null);
	}
	
	//Sample test
	@Test
	public void testExample() {
		assertTrue(true);
	}
}
