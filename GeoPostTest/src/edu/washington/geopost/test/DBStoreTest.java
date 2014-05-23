package edu.washington.geopost.test;

import org.junit.Test;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseObject;

import edu.washington.geopost.DBStore;
import edu.washington.geopost.ParsePin;
import edu.washington.geopost.Pin;

/**
 * 
 * DBStoreTest contains unit tests for the functionality of the DBStore class.
 * @author Andrew Repp, Neil Hinnant
 *
 */

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

public class DBStoreTest extends AndroidTestCase {
	
	private static DBStore pinWriter;
	private String appID;
	private String clientKey;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		appID = "GlrWxWCu5mnGFKUeeQIFg9Upt9AwomBDk3t0OKHa";
		clientKey = "HRRt6k8GzTclufgMCW8RES8LZgQLTTvKBJAnbD5c";
		Parse.initialize(getContext(), appID, clientKey);
		ParseObject.registerSubclass(ParsePin.class);
		pinWriter = new DBStore ();
	}
	
	//Test a single pin drop
	@Test
	public void testSinglePin() {
		String message = "This is a test pin";
		LatLng coord = new LatLng(35.445, 47.555);
		Pin pin = pinWriter.postPin(coord, message, null);
		
		assertTrue(message.equals(pin.getMessage()));
		assertTrue(coord.equals(pin.getLocation()));
	}
	
	//Test a multiple pin drop
	@Test
	public void testMultiPin() {
		LatLng coord1 = new LatLng(25.225, 56.553);
		LatLng coord2 = new LatLng(23.455, 57.898);
		LatLng coord3 = new LatLng(26.999, 58.550);
		
		Pin pin1 = pinWriter.postPin(coord1, "Pin1", null);
		Pin pin2 = pinWriter.postPin(coord2, "Pin2", null);
		Pin pin3 = pinWriter.postPin(coord3, "Pin3", null);
		
		assertTrue("Pin1".equals(pin1.getMessage()));
		assertTrue(coord1.equals(pin1.getLocation()));
		
		assertTrue("Pin2".equals(pin2.getMessage()));
		assertTrue(coord2.equals(pin2.getLocation()));
		
		assertTrue("Pin3".equals(pin3.getMessage()));
		assertTrue(coord3.equals(pin3.getLocation()));
	}
	
	//TODO
	//Test a duplicate location pin drop (with different messages)
	//What is the behavior here? I'm not sure we've defined it. 
	//I seem to recall we wanted dupes to overwrite.
	
	//This might actually be a system test? It requires using DBQuery
//	@Test
//	public void testDuplicatePin() {
//		LatLng coord = new LatLng(47.325, -18.25);
//		Pin first = pinWriter.postPin(coord, "First Message");
//		Pin second = pinWriter.postPin(coord, "Second Message");
//	}
	
	//Test a null pin drop
	@Test 
	public void testNullPin() {
		LatLng nullCoord = null;
		Pin nullPin = pinWriter.postPin(nullCoord, "Sooooo null", null);
		
		assertTrue(nullPin == null);
	}
	
	//Test null message
	@Test
	public void testNullMessage() {
		LatLng coord = new LatLng(53.22,56.22);
		Pin nullPin = pinWriter.postPin(coord, null, null);
		
		assertTrue(nullPin == null);
	}
	
	//Test single pin unlock
	@Test 
	public void testSingleUnlock() {
		String message = "Unlock Me!";
		LatLng coord = new LatLng(38.55, -47.885);
		Pin pin = pinWriter.postPin(coord, message, null);
		
		assertTrue(pin != null);
		
		Pin uPin = pinWriter.unlockPin(pin);
		assertTrue(uPin != null);
		assertTrue(!uPin.isLocked());
	}
	
	//TODO I think we have a problem here.. How can I test whether or 
	//not we're unlocking a pin we're allowed to? That logic may be improperly 
	//abstracted?
	//Test multiple pin unlock
	@Test
	public void testMultiUnlock() {
		LatLng coord1 = new LatLng(25.225, 56.553);
		LatLng coord2 = new LatLng(23.455, 57.898);
		LatLng coord3 = new LatLng(26.999, 58.550);
		
		Pin pin1 = pinWriter.postPin(coord1, "Pin1", null);
		Pin pin2 = pinWriter.postPin(coord2, "Pin2", null);
		Pin pin3 = pinWriter.postPin(coord3, "Pin3", null);
		
		assertTrue(pin1 != null);
		assertTrue(pin2 != null);
		assertTrue(pin3 != null);
		
		Pin uPin1 = pinWriter.unlockPin(pin1);
		assertTrue(uPin1 != null);
		assertTrue(!uPin1.isLocked());

		Pin uPin2 = pinWriter.unlockPin(pin2);
		assertTrue(uPin2 != null);
		assertTrue(!uPin2.isLocked());

		Pin uPin3 = pinWriter.unlockPin(pin3);
		assertTrue(uPin3 != null);
		assertTrue(!uPin3.isLocked());
	}
	
	//Test unlock null pin
	@Test
	public void testNullUnlock() {
		assertTrue(pinWriter.unlockPin(null) == null);
	}
	
	//Sample test
	@Test
	public void testExample() {
		// I SAW THIS TEST FAIL
		// IS THIS EVEN JAVA
		assertTrue(true);
	}
}
