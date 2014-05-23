package edu.washington.geopost.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import edu.washington.geopost.DBQuery;
import edu.washington.geopost.ParsePin;
import edu.washington.geopost.Pin;
import edu.washington.geopost.User;

/**
 * 
 * DBQueryTest contains unit tests for the functionality of the DBQuery class.
 * @author Andrew Repp, Katie Madonna
 *
 */

// We need to consider how to set up a user for testing. One thing to
// look into would be the ParseAnonymousUtils class, which has documentation at
// https://parse.com/docs/android/api/com/parse/ParseAnonymousUtils.html
// It might be able to help.

public class DBQueryTest extends AndroidTestCase {
	private static final int NUMBER_OF_TESTS = 3; // one additional for testAndroidTestCaseSetupProperly
	private static int testsRun = 0;
	static List<ParseObject> createdObjs = new ArrayList<ParseObject>();
	private final String appID = "GlrWxWCu5mnGFKUeeQIFg9Upt9AwomBDk3t0OKHa";
	private final String clientKey = "HRRt6k8GzTclufgMCW8RES8LZgQLTTvKBJAnbD5c";
//	private final String facebookAppID = "248684115322840";
	private ParseUser testUser;
	private String parseUserEmail = "parsetestuser@huehuehue.com";
	private String parseUserName = "Parse Test User";
	private String parsePassword = "testPassword1234";
//	private String parseTestFBName = "Parse Test User Name";
//	private String parseTestFBID = "123456654321";
	private ParsePin testPin0;
	private ParsePin testPin1;
	private ParsePin testPin2;
	
	
	/**
	 * Set up before each test case runs.
	 */
	@Override
	public void setUp() throws Exception {
		testsRun++;
		Parse.initialize(getContext(), appID, clientKey);
		ParseObject.registerSubclass(ParsePin.class);
		super.setUp();
		
		testUser = new ParseUser();
		testUser.setEmail(parseUserEmail);
		testUser.setUsername(parseUserName);
		testUser.setPassword(parsePassword);
		
		ParseUser.logOut();
		try {
			testUser.signUp();
		} catch (ParseException e) {
			if (e.getCode() != e.USERNAME_TAKEN){
				throw e;
			}
		}
		
		ParseUser.logIn(parseUserName, parsePassword);
		
		testUser = ParseUser.getCurrentUser();
		
		// Create several pins 
		// Try to add each to the database
		testPin0 = new ParsePin();
		testPin0.setUser(testUser);
		testPin0.setLocation(new ParseGeoPoint(0.0,0.1));
		testPin0.setMessage("Sample Message 1");
		createdObjs.add(testPin0);
		
		try {
			testPin0.save();
		} catch (ParseException e) {
			Log.d("mybugs", "Couldn't save pin.");
			e.printStackTrace();
		}
		
		testPin1 = new ParsePin();
		testPin1.setUser(testUser);
		testPin1.setLocation(new ParseGeoPoint(0.1,0.2));
		testPin1.setMessage("Sample Message 2");
		createdObjs.add(testPin1);
		
		try {
			testPin1.save();
		} catch (ParseException e) {
			Log.d("mybugs", "Couldn't save pin.");
			e.printStackTrace();
		}
		
		testPin2 = new ParsePin();
		testPin2.setUser(testUser);
		testPin2.setLocation(new ParseGeoPoint(0.2,0.3));
		testPin2.setMessage("Sample Message 3");
		createdObjs.add(testPin2);
		
		try {
			testPin2.save();
		} catch (ParseException e) {
			Log.d("mybugs", "Couldn't save pin.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Tear down after each test case runs.
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		// Delete all the elements added to the database after all tests run.
		if (testsRun == NUMBER_OF_TESTS) {
			try {
				ParseObject.deleteAll(createdObjs);
			} catch (ParseException e) {
				Log.d("mybugs", "Couldn't delete objects from the database.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Test for DBQuery getCurrentUser method.
	 */
	@Test
	public void testCurrentUser() {
		DBQuery dbq = new DBQuery();
		User user = dbq.getCurrentUser();
		
		Log.d("LC", "USERNAME: " + user.getName() + " testusername: " + testUser.getString("name"));
		assertTrue(user.getName().equals(testUser.getUsername()));
		assertTrue(user.getFacebookID().equals(testUser.getString("facebookID")));
		// TODO: Fix this when relations are being tested.
		//assertTrue(user.getNumPosted() == 3);
		//assertTrue(user.getNumViewed() == 0);
	}
	
	/**
	 * Test for DBQuery getPins method.
	 */
	@Test
	public void testGetPins() {
		DBQuery dbq = new DBQuery();
		Set<Pin> pins = dbq.getPins(new LatLng(0.0, 0.0), new LatLng(0.4, 0.4));
		
		assertTrue(pins.size() > 2);
		
		// Ideally we would check for the actual pins being in the db, but the equals method 
		// only checks for equivalent IDs. This is the best I got for now.
		/*
		assertTrue(pins.contains(testPin0));
		assertTrue(pins.contains(testPin1));
		assertTrue(pins.contains(testPin2));
		*/
	}
}
