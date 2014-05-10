package edu.washington.geopost;

import java.util.List;
import java.util.Set;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import android.location.Location;

public class DBQuery {
	/**
	 * 
	 * @param southWest The lower left corner of the user's screen on the map
	 * @param northEast The upper right corner of the user's screen on the map
	 * @return A set of the pins within the given box made with passed coordinates
	 */
	public Set<Pin> getPins(Location southWest, Location northEast) {
		ParseGeoPoint sw = new ParseGeoPoint(southWest.getLatitude(), 
											 southWest.getLongitude());
		ParseGeoPoint ne = new ParseGeoPoint(northEast.getLatitude(),
											 northEast.getLongitude());
		ParseQuery<ParseObject> pinQuery = ParseQuery.getQuery("ParseQuery"); // TODO: Switch this to ParsePin.class
		pinQuery.whereWithinGeoBox("location", sw, ne);
		
		List<ParseObject> queryResults = null;
		try {
			queryResults = pinQuery.find();
		} catch (ParseException e) { // TODO: Make this more robust
			System.out.println("fetching pins had an error");
		}
		
		if (queryResults != null) {
			// TODO: Process ParsePins, convert to Pins, add to Set
		}
		return null;
	}
	
	/**
	 * Returns a User object containing information about the current user of
	 * the app.
	 * @param userId The user ID of the user we want information about
	 * @return The User that we want information about
	 */
	public User getUser(String userId) {
		String name = null;
		int viewedPosts = 0;
		int postedPosts = 0;
		
		// Fetch the current user and get the number of pins they've viewed and
		// posted
		ParseUser user = ParseUser.getCurrentUser();
		ParseRelation<ParseObject> viewedRelation = user.getRelation("viewed");
		try {
			viewedPosts = viewedRelation.getQuery().count();
		} catch (ParseException e) { // TODO: Make this more robust
			System.out.println("fetching viewed had an error");
		}
		ParseRelation<ParseObject> postedRelation = user.getRelation("posted");
		try {
			postedPosts = postedRelation.getQuery().count();
		} catch (ParseException e) { // TODO: Make this more robust
			System.out.println("error fetching posted");
		}
		
		// TODO: Connect with Facebook to get user name. We will need to set up
		// Facebook for our app.
		
		// TODO: Create and return new User object
		return null;
	}
}
