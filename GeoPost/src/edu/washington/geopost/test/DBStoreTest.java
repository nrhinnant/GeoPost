package edu.washington.geopost.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import edu.washington.geopost.DBStore;
import edu.washington.geopost.ParsePin;
import edu.washington.geopost.Pin;

/**
 * DBStoreTest contains unit tests for the functionality of the DBStore class.
 * @authors Neil Hinnant, Katie Madonna, Andrew Repp
 */

public class DBStoreTest extends AndroidTestCase {
	private static final String APP_ID = "GlrWxWCu5mnGFKUeeQIFg9Upt9AwomBDk3t0OKHa";
	private static final String CLIENT_KEY = "HRRt6k8GzTclufgMCW8RES8LZgQLTTvKBJAnbD5c";
	
	// User login information
	private static final String PARSE_USER_EMAIL = "parsetestuser@huehuehue.com";
	private static final String PARSE_USERNAME = "Parse Test User";
	private static final String PARSE_PASSWORD = "testPassword1234";

	private static final String PARSE_USER_EMAIL_2 = "parsetestuser2@cse403.com";
	private static final String PARSE_USERNAME_2 = "Parse Test User 2";
	private static final String PARSE_PASSWORD_2 = "testPassword5678";
	
	private static DBStore pinWriter;
	private static List<ParseObject> createdObjs;  // For removal of pins.
	private static ParseQuery<ParsePin> query;
	private static ParseUser testUser;
	
	/**
	 *  Sets up the test class for each individual test.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		Parse.initialize(getContext(), APP_ID, CLIENT_KEY);
		ParseObject.registerSubclass(ParsePin.class);
		pinWriter = new DBStore();
		createdObjs = new ArrayList<ParseObject>();
		query = ParseQuery.getQuery(ParsePin.class);
		
		ParseUser.logOut();
		
		testUser = new ParseUser();
		testUser.setEmail(PARSE_USER_EMAIL);
		testUser.setUsername(PARSE_USERNAME);
		testUser.setPassword(PARSE_PASSWORD);
		try {
			testUser.signUp();
		} catch (ParseException e) {
			if (e.getCode() != ParseException.USERNAME_TAKEN) {
				Log.d("DBStoreTest setUp", "error signing up user, but user"
						+ "name not already taken");
				throw e;
			}
		}
		
		try {
			ParseUser.logIn(PARSE_USERNAME, PARSE_PASSWORD);
		} catch (ParseException e) {
			Log.d("DBStoreTest setUp", "error logging in user");
			throw e;
		}
		
		testUser = ParseUser.getCurrentUser();
	}
	
	/**
	 * Tears down the test class after each individual test.
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		
		// Delete all the elements added to the database after the test ran.
		try {
			ParseObject.deleteAll(createdObjs);
		} catch (ParseException e) {
			Log.d("DBStoreTest tearDown", "Couldn't delete objects from the"
					+ "database.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Tests that adding a single pin to the database works correctly.
	 */
	@Test
	public void testSinglePin() {
		String message = "This is a test pin";
		LatLng coord = new LatLng(35.445, 47.555);
		Pin pin = pinWriter.postPin(coord, message, null);
		
		// Add the created Pin to the list so it can be
		// removed at the end of the test.
		try {
			createdObjs.add(query.get(pin.getPinId()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		assertNotNull(pin);
		assertFalse(pin.isLocked());
		assertEquals(coord, pin.getLocation());
		assertEquals(testUser.getUsername(), pin.getUser());
		assertEquals(message, pin.getMessage());
		assertNull(pin.getPhoto());
	}
	
	/**
	 * Tests that adding multiple pins to the database in a row works 
	 * correctly.
	 */
	@Test
	public void testMultiPin() {
		LatLng coord1 = new LatLng(25.225, 56.553);
		LatLng coord2 = new LatLng(23.455, 57.898);
		LatLng coord3 = new LatLng(26.999, 58.550);
		
		Pin pin1 = pinWriter.postPin(coord1, "Pin1", null);
		Pin pin2 = pinWriter.postPin(coord2, "Pin2", null);
		Pin pin3 = pinWriter.postPin(coord3, "Pin3", null);
		
		// Add the created Pins to the list so they can be
		// removed at the end of the test.
		try {
			createdObjs.add(query.get(pin1.getPinId()));
			createdObjs.add(query.get(pin2.getPinId()));
			createdObjs.add(query.get(pin3.getPinId()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		assertNotNull(pin1);
		assertFalse(pin1.isLocked());
		assertEquals(coord1, pin1.getLocation());
		assertEquals(testUser.getUsername(), pin1.getUser());
		assertEquals("Pin1", pin1.getMessage());
		assertNull(pin1.getPhoto());

		assertNotNull(pin2);
		assertFalse(pin2.isLocked());
		assertEquals(coord2, pin2.getLocation());
		assertEquals("Pin2", pin2.getMessage());
		assertEquals(testUser.getUsername(), pin2.getUser());
		assertNull(pin2.getPhoto());

		assertNotNull(pin3);
		assertFalse(pin3.isLocked());
		assertEquals(coord3, pin3.getLocation());
		assertEquals("Pin3", pin3.getMessage());
		assertEquals(testUser.getUsername(), pin2.getUser());
		assertNull(pin2.getPhoto());
	}
	
	/**
	 * Tests that saving two pins at the same coordinates works correctly.
	 */
	@Test
	public void testDuplicatePin() {
		LatLng coord = new LatLng(47.325, -18.25);
		Pin first = pinWriter.postPin(coord, "First Message", null);
		Pin second = pinWriter.postPin(coord, "Second Message", null);
		
		// Add the created Pins to the list so they can be
		// removed at the end of the test.
		try {
			createdObjs.add(query.get(first.getPinId()));
			createdObjs.add(query.get(second.getPinId()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		assertNotNull(first);
		assertFalse(first.isLocked());
		assertEquals(coord, first.getLocation());
		assertEquals("First Message", first.getMessage());
		assertEquals(testUser.getUsername(), first.getUser());
		assertNull(first.getPhoto());

		assertNotNull(second);
		assertFalse(second.isLocked());
		assertEquals(coord, second.getLocation());
		assertEquals("Second Message", second.getMessage());
		assertEquals(testUser.getUsername(), second.getUser());
		assertNull(second.getPhoto());
	}
	
	/**
	 * Tests that trying to post a pin with null coordinates fails.
	 */
	@Test 
	public void testNullCoord() {
		Pin nullCoordPin = pinWriter.postPin(null, "Sooooo null", null);
		
		assertNull(nullCoordPin);
	}
	
	/**
	 * Tests that trying to post a pin with a null message fails.
	 */
	@Test
	public void testNullMessage() {
		LatLng coord = new LatLng(53.22, 56.22);
		Pin nullMessagePin = pinWriter.postPin(coord, null, null);
		
		assertNull(nullMessagePin);
	}
	
	/**
	 * Tests that unlocking a single pin works correctly.
	 */
	@Test
	public void testSingleUnlock() {
		// Get a different user to post the Pin
		ParseUser postUser = switchToPostUser();
		assertNotNull(postUser);
		
		// Create the Pin
		String message = "Unlock Me!";
		LatLng coord = new LatLng(38.55, -47.885);
		Pin pin = pinWriter.postPin(coord, message, null);
		assertNotNull(pin);
		
		// Create a locked version for the test user to unlock
		Pin lockedPin = getLockedVersionOfPin(pin);
	
		// Add the created Pin to the list so it can be
		// removed at the end of the test.
		try {
			createdObjs.add(query.get(pin.getPinId()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Switch back to the test user to unlock the pin
		assertTrue(switchToTestUser());
		
		// Check that the pin is locked 
		assertNotNull(lockedPin);
		assertTrue(lockedPin.isLocked());
		
		// Do the unlocking and check that it worked
		Pin unlockedPin = pinWriter.unlockPin(lockedPin);
		assertNotNull(unlockedPin);
		assertFalse(unlockedPin.isLocked());
	}
	

	/**
	 * Tests that unlocking multiple pins in a row works correctly.
	 */
	@Test
	public void testMultiUnlock() {
		// Get a different user to post the Pin
		ParseUser postUser = switchToPostUser();
		assertNotNull(postUser);
		
		// Create the Pins
		LatLng coord1 = new LatLng(25.225, 56.553);
		LatLng coord2 = new LatLng(23.455, 57.898);
		LatLng coord3 = new LatLng(26.999, 58.550);
		
		Pin pin1 = pinWriter.postPin(coord1, "Pin1", null);
		Pin pin2 = pinWriter.postPin(coord2, "Pin2", null);
		Pin pin3 = pinWriter.postPin(coord3, "Pin3", null);
		
		assertNotNull(pin1);
		assertNotNull(pin2);
		assertNotNull(pin3);
		
		// Create locked versions for the test user to unlock
		Pin lockedPin1 = getLockedVersionOfPin(pin1);
		Pin lockedPin2 = getLockedVersionOfPin(pin2);
		Pin lockedPin3 = getLockedVersionOfPin(pin3);
		
		// Add the created Pins to the list so they can be
		// removed at the end of the test
		try {
			createdObjs.add(query.get(pin1.getPinId()));
			createdObjs.add(query.get(pin2.getPinId()));
			createdObjs.add(query.get(pin3.getPinId()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Switch back to the test user to unlock the pins
		assertTrue(switchToTestUser());
		
		// Check that the pins are locked
		assertNotNull(lockedPin1);
		assertTrue(lockedPin1.isLocked());
		assertNotNull(lockedPin2);
		assertTrue(lockedPin2.isLocked());
		assertNotNull(lockedPin3);
		assertTrue(lockedPin3.isLocked());
		
		// Do the unlocking and check that it worked
		Pin uPin1 = pinWriter.unlockPin(lockedPin1);
		assertNotNull(uPin1);
		assertFalse(uPin1.isLocked());

		Pin uPin2 = pinWriter.unlockPin(lockedPin2);
		assertNotNull(uPin2);
		assertFalse(uPin2.isLocked());

		Pin uPin3 = pinWriter.unlockPin(lockedPin3);
		assertNotNull(uPin3);
		assertFalse(uPin3.isLocked());
	}
	
	/**
	 * Tests that unlocking a null pin fails.
	 */
	@Test
	public void testNullUnlock() {
		assertNull(pinWriter.unlockPin(null));
	}
	
	/**
	 * Switches to a different user in order to have a different user post a
	 * Pin than the testUser.
	 * @return a user suitable for posting Pins (different from testUser)
	 */
	private ParseUser switchToPostUser() {
		ParseUser.logOut();
		
		ParseUser postUser = new ParseUser();
		
		postUser.setEmail(PARSE_USER_EMAIL_2);
		postUser.setUsername(PARSE_USERNAME_2);
		postUser.setPassword(PARSE_PASSWORD_2);
		try {
			postUser.signUp();
		} catch (ParseException e) {
			if (e.getCode() != ParseException.USERNAME_TAKEN) {
				e.printStackTrace();
				return null;
			}
		}
		
		try {
			ParseUser.logIn(PARSE_USERNAME_2, PARSE_PASSWORD_2);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return postUser;
	}
	
	/**
	 * Logs out the current user and switches to the testUser.
	 * @return true if the testUser was successfully logged in, false otherwise
	 */
	private boolean switchToTestUser() {
		ParseUser.logOut();
		
		try {
			ParseUser.logIn(PARSE_USERNAME, PARSE_PASSWORD);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Returns a locked version of the given Pin.
	 * @param pin The Pin to get a locked version of
	 * @return A copy of the given Pin marked as locked
	 */
	private Pin getLockedVersionOfPin(Pin pin) {
		return new Pin(true, pin.getLocation(), pin.getUser(), 
					   pin.getFacebookID(), pin.getPinId(), pin.getMessage(),
					   pin.getPhoto());
	}
}
