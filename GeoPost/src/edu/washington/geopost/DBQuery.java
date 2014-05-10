package edu.washington.geopost;

import java.util.HashSet;
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
	 * Returns all the pins located within the box bounded by the given corners.
	 * @param southWest The lower left corner of the user's screen on the map
	 * @param northEast The upper right corner of the user's screen on the map
	 * @return A set of the pins within the given box made with passed coordinates
	 */
	public Set<Pin> getPins(Location southWest, Location northEast) {
		ParseGeoPoint sw = new ParseGeoPoint(southWest.getLatitude(), 
											 southWest.getLongitude());
		ParseGeoPoint ne = new ParseGeoPoint(northEast.getLatitude(),
											 northEast.getLongitude());
		ParseQuery<ParsePin> pinQuery = ParseQuery.getQuery("ParsePin");
		pinQuery.whereWithinGeoBox("location", sw, ne);
		
		List<ParsePin> queryResults = null;
		try {
			queryResults = pinQuery.find();
		} catch (ParseException e) { // TODO: Make this more robust
			System.out.println("fetching pins had an error");
		}
		
		Set<Pin> pinsToDisplay = null;
		if (queryResults != null) {
			// TODO: Process ParsePins, convert to Pins (including setting
			// locked status), add to Set
			pinsToDisplay = new HashSet<Pin>();
			for (ParsePin pin : queryResults) {
				boolean locked = true; // TODO: determine how to get using pin.getUser().getUsername();

				// Set up the location.
				// TODO: Do we need to set bearing and accuracy???
				Location location = new Location("");
		        location.setLatitude(pin.getLocation().getLatitude());
		        location.setLongitude(pin.getLocation().getLongitude());

				Pin newPin = new Pin(locked,
						location,
						pin.getUser().getUsername(),
						pin.getObjectId(),
						pin.getMessage());

				pinsToDisplay.add(newPin);
			}
		}
		return pinsToDisplay;
	}
	
	/**
	 * Returns a User object containing information about the current user of
	 * the app.
	 * @return The User that we want information about
	 */
	public User getCurrentUser() {
		String name = null;
		int viewedNum = 0;
		int postedNum = 0;
		
		// Fetch the current user
		ParseUser user = ParseUser.getCurrentUser();
		
		// TODO: Connect with Facebook to get user name. We will need to set up
		// Facebook for our app. Right now it just returns the Parse username.
		name = user.getUsername();
		
		// Get the number of pins they've viewed
		ParseRelation<ParseObject> viewedRelation = user.getRelation("viewed");
		try {
			viewedNum = viewedRelation.getQuery().count();
		} catch (ParseException e) { // TODO: Make this more robust
			System.out.println("fetching viewed had an error");
		}
		
		// Get the number of pins they've posted
		ParseRelation<ParseObject> postedRelation = user.getRelation("posted");
		try {
			postedNum = postedRelation.getQuery().count();
		} catch (ParseException e) { // TODO: Make this more robust
			System.out.println("error fetching posted");
		}
		
		return new User(viewedNum, postedNum, name);
	}
}
