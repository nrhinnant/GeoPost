package edu.washington.geopost.test;

import java.util.ArrayList;
import java.util.List;
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
import com.parse.ParseRelation;
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

// TODO: This class will be hard to test.
// We need to consider how to set up a user for testing. One thing to
// look into would be the ParseAnonymousUtils class, which has documentation at
// https://parse.com/docs/android/api/com/parse/ParseAnonymousUtils.html
// It might be able to help.

public class DBQueryTest extends AndroidTestCase {
	private static final int NUMBER_OF_TESTS = 3; // one additional for testAndroidTestCaseSetupProperly
	private static int testsRun = 0;
	static List<ParseObject> createdObjs = new ArrayList<ParseObject>();
	private final static String appID = "";  // Insert Parse ApplicationID
	private final static String clientKey = "";  // Insert Parse ClientKey
	private final static String facebookAppID = "";  // Insert Facebook ApplicationID
	
	/**
	 * Set up before each test case runs.
	 */
	@Override
	public void setUp() throws Exception {
		testsRun++;
		Parse.initialize(getContext(), appID, clientKey);
		ParseObject.registerSubclass(ParsePin.class);
		super.setUp();
		
		// Check for a current parse user.
		ParseUser user = ParseUser.getCurrentUser();
		
		// Create a new user and pins the first time setUp is called.
		if (user == null) {
			user = new ParseUser();  // test user
			user.setUsername("name");
			user.setPassword("password");
			user.setEmail("email@example.com");
			
			createdObjs.add(user);  // so they can be removed from database
			Log.d("mybugs", "I am running.");
			
			// Sign up the test user.
			try {
				user.signUp();
				Log.d("mybugs", "User signed up.");
			} catch (ParseException e) {
				// Sign up failed.
				Log.d("mybugs", "Error signing up user.");
				e.printStackTrace();
			}
			
			ParseRelation<ParsePin> viewedPins = user.getRelation("viewed");
			ParseRelation<ParsePin> postedPins = user.getRelation("posted");

			// Create several pins 
			// Try to add each to the database
			ParsePin dbp1 = new ParsePin();
			dbp1.setUser(user);
			dbp1.setLocation(new ParseGeoPoint(0.0,0.1));
			dbp1.setMessage("Sample Message 1");
			createdObjs.add(dbp1);
			
			try {
				dbp1.save();
			} catch (ParseException e) {
				Log.d("mybugs", "Couldn't save pin.");
				e.printStackTrace();
			}
			
			viewedPins.add(dbp1);
			postedPins.add(dbp1);
			
			ParsePin dbp2 = new ParsePin();
			dbp2.setUser(user);
			dbp2.setLocation(new ParseGeoPoint(0.1,0.2));
			dbp2.setMessage("Sample Message 2");
			createdObjs.add(dbp2);
			
			try {
				dbp2.save();
			} catch (ParseException e) {
				Log.d("mybugs", "Couldn't save pin.");
				e.printStackTrace();
			}
			
			viewedPins.add(dbp2);
			postedPins.add(dbp2);
			
			ParsePin dbp3 = new ParsePin();
			dbp3.setUser(user);
			dbp3.setLocation(new ParseGeoPoint(0.2,0.3));
			dbp3.setMessage("Sample Message 3");
			createdObjs.add(dbp3);
			
			try {
				dbp3.save();
			} catch (ParseException e) {
				Log.d("mybugs", "Couldn't save pin.");
				e.printStackTrace();
			}
			
			viewedPins.add(dbp3);
			postedPins.add(dbp3);
		}
	}
	
	/**
	 * Tear down after each test case runs.
	 */
	@Override
	public void tearDown() throws Exception {
		Parse.initialize(getContext(), appID, clientKey);
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
		Parse.initialize(getContext(), appID, clientKey);
		ParseFacebookUtils.initialize(facebookAppID);
		DBQuery dbq = new DBQuery();
		User user = dbq.getCurrentUser();
		
		assertTrue(user.getName().equals("name"));
		// TODO: Fix this when relations are being tested.
		//assertTrue(user.getNumPosted() == 3);
		//assertTrue(user.getNumViewed() == 0);
	}
	
	/**
	 * Test for DBQuery getPins method.
	 */
	@Test
	public void testGetPins() {
		Parse.initialize(getContext(), appID, clientKey);
		DBQuery dbq = new DBQuery();
		Set<Pin> pins = dbq.getPins(new LatLng(0.0, 0.0), new LatLng(0.4, 0.4));
		
		assertTrue(pins.size() == 3);
	}
}
