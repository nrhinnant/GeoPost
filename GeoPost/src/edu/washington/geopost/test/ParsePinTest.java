package edu.washington.geopost.test;

import org.junit.Test;

import android.test.AndroidTestCase;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import edu.washington.geopost.ParsePin;

/**
 * ParsePinTest contains the unit test cases for the ParsePin class.
 * @author Katie Madonna
 *
 */

public class ParsePinTest extends AndroidTestCase {
	private static final String APP_ID = "GlrWxWCu5mnGFKUeeQIFg9Upt9AwomBDk3t0OKHa";
	private static final String CLIENT_KEY = "HRRt6k8GzTclufgMCW8RES8LZgQLTTvKBJAnbD5c";
	
	// Sample pins for the test.
	private ParsePin pin0;
	private ParsePin pin1;
	private ParseUser user;
	
	// Sample fields to put in the pins.
	private static final String SAMPLE_OID = "Pin1OID";
	private static final String SAMPLE_MESSAGE = "Sample Message";
	private static final byte[] EMPTY_BYTES = new byte[0];
	private static final ParseFile SAMPLE_PHOTO = new ParseFile(EMPTY_BYTES);
	private static final ParseGeoPoint SAMPLE_GP = new ParseGeoPoint();
	private static final String USERNAME = "Username";
	
	/**
	 * Sets up two pins, one with all empty fields and one with all
	 * fields initialized. 
	 * @throws Exception if android test case setUp fails.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		Parse.initialize(getContext(), APP_ID, CLIENT_KEY);
		ParseObject.registerSubclass(ParsePin.class);
		
		// Sample user to store in a pin
		user = new ParseUser();
		user.setUsername(USERNAME);
		
		// An empty pin
		pin0 = new ParsePin();
		 
		// A sample pin with fields set
		pin1 = new ParsePin();
		pin1.setObjectId(SAMPLE_OID);
		pin1.setMessage(SAMPLE_MESSAGE);
		pin1.setPhoto(SAMPLE_PHOTO);
		pin1.setLocation(SAMPLE_GP);
		pin1.setUser(user);

	}
	
	/**
	 * Test the get message method for an empty and non-empty pin.
	 */
	@Test
	public void testGetMessage() {
		assertNull(pin0.getMessage());
		assertEquals(SAMPLE_MESSAGE, pin1.getMessage());
	}
	
	/**
	 * Test the set message method for an empty and non-empty pin.
	 */
	@Test
	public void testSetMessage() {	
		// Verify initial value
		assertEquals(SAMPLE_MESSAGE, pin1.getMessage());
		
		// Set new messages for each pin.
		pin0.setMessage("Pin0 New Message");
		pin1.setMessage("Pin1 New Message");
		
		// Check that the messages were set properly
		assertEquals("Pin0 New Message", pin0.getMessage());
		assertEquals("Pin1 New Message", pin1.getMessage());
	}
	
	/**
	 * Test the get user method for both empty and non-empty pins.
	 */
	@Test
	public void testGetUser() {
		assertNull(pin0.getUser());
		assertEquals(user, pin1.getUser());
	}
	
	/**
	 * Test the set user method for an empty pin.
	 */
	@Test
	public void testSetUser() {
		// Verify initial state
		assertNull(pin0.getUser());
		
		// Make a new sample user and set the pin to have this user
		ParseUser user2 = new ParseUser();
		pin0.setUser(user2);
		
		assertEquals(user2, pin0.getUser());
	}
	
	/**
	 * Test the get photo method for an empty and non-empty pin.
	 */
	@Test
	public void testGetPhoto() {
		assertNull(pin0.getPhoto());
		assertEquals(SAMPLE_PHOTO, pin1.getPhoto());
	}
	
	/**
	 * Test the set photo method for an empty pin.
	 */
	@Test
	public void testSetPhoto() {
		ParseFile photo = new ParseFile(EMPTY_BYTES);
		pin0.setPhoto(photo);
		
		assertEquals(photo, pin0.getPhoto());
	}
	
	/**
	 * Test the get location method for an empty and non-empty pin.
	 */
	@Test
	public void testGetLocation() {
		assertNull(pin0.getLocation());
		assertEquals(SAMPLE_GP, pin1.getLocation());
	}
	
	/**
	 * Test the set photo for an empty pin.
	 */
	@Test
	public void testSetLocation() {
		ParseGeoPoint gp = new ParseGeoPoint();
		pin0.setLocation(gp);
		
		assertEquals(gp, pin0.getLocation());
	}
	
	/**
	 * Tests equals for the same pin and two different pins with the same
	 * objectID. 
	 */
	@Test
	public void testEquals() {
		// Same pins
		assertEquals(pin0, pin0);
		assertEquals(pin1, pin1);
		
		// Different pins with same OID
		pin0.setObjectId(SAMPLE_OID);
		assertEquals(pin0.hashCode(), pin1.hashCode());
	}
	
	/**
	 * Tests hashCode for the same pin and two different pins with the same
	 * objectID. 
	 */
	@Test
	public void testHashCode() {
		pin0.setObjectId(SAMPLE_OID);
		assertEquals(pin1.hashCode(), pin1.hashCode());
		assertEquals(pin0.hashCode(), pin1.hashCode());
	}
	
	/**
	 * Checks that hashCode on a pin with a null objectID is null.
	 */
	@Test
	public void testNullOIDHashCode() {
		// OID is null, hashCode relies on OID's hashCode, so we expect an
		// exception but android jUnit does not support expect=
		try {
			pin0.hashCode();
			assertTrue(false);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Test to string for an empty and non-empty pin.
	 */
	@Test
	public void testToString() {
		assertNull(pin0.toString());
		assertEquals(SAMPLE_OID, pin1.toString());
	}
}
