package edu.washington.geopost;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.android.gms.maps.model.LatLng;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;


/**
 * 
 * DBQuery retrieves information from the Parse database using parameterized
 * queries.
 * 
 * @authors Katie Madonna, Andrew Repp
 * 
 */

public class DBQuery {
	/**
	 * Returns all of the pins located within the box bounded by the given 
	 * corners.
	 * @param southWest The lower left corner of the box for which pins are
	 *                  desired
	 * @param northEast The upper right corner of the box for which pins are
	 *                  desired
	 * @return A set of the Pins within the given box. The set will be empty if
	 *         no such Pins are found or there is an error fetching data.
	 */
	public Set<Pin> getPins(LatLng southWest, LatLng northEast) {
		// The returned set
		Set<Pin> pinsToDisplay = new HashSet<Pin>();
		
		// Convert the Locations to ParseGeoPoints
		ParseGeoPoint sw = new ParseGeoPoint(southWest.latitude, 
											 southWest.longitude);
		ParseGeoPoint ne = new ParseGeoPoint(northEast.latitude,
											 northEast.longitude);
		
		
		// Set up the ParseQuery for the pins within the given coordinates
		ParseQuery<ParsePin> pinQuery = ParseQuery.getQuery(ParsePin.class);
		
		pinQuery.whereWithinGeoBox("location", sw, ne);
		
		// Get the results of the query
		List<ParsePin> queryResults = null;
		try {
			queryResults = pinQuery.find();
		} catch (ParseException e) {
			// Fetching pins had an error
			return null;
		}
		
		if (queryResults != null) { // The query was successful
			// Fetch the current user and set up the query for the list of pins
			// they've viewed inside the bounding box.
			ParseUser user = ParseUser.getCurrentUser();
			ParseRelation<ParsePin> viewedRelation = user.getRelation("viewed");
			ParseQuery<ParsePin> viewedQuery = viewedRelation.getQuery();
			viewedQuery.whereWithinGeoBox("location", sw, ne);
			
			// Execute the query
			List<ParsePin> viewed = new ArrayList<ParsePin>();
			try {
				viewed = viewedQuery.find();
			} catch (ParseException e) {
				// Fetching viewed had an error
				return null;
			}
			
			for (ParsePin pin : queryResults) {
				// If this pin is in the user's list of viewed pins, then we'll
				// mark the pin as unlocked. Otherwise, we'll set it as locked.
				// TODO: Will this be fast enough? Also, do we want to do
				// something special if there's an error fetching the user's
				// viewed list?
				boolean locked = viewed.contains(pin);

				// Set up the pin's location.
				LatLng location = new LatLng(pin.getLocation().getLatitude(),
						pin.getLocation().getLongitude());

		        // Make the new pin and add it to the result set
				Pin newPin = new Pin(locked, location, 
									 "anonymous",
									 pin.getObjectId(), pin.getMessage());

				pinsToDisplay.add(newPin);
			}
		}
		return pinsToDisplay;
	}
	
	/**
	 * Returns a User object containing information about the current user of
	 * the app.
	 * @return The User about which information is desired
	 */
	public User getCurrentUser() {
		String name = null;
		int viewedNum = 0;
		int postedNum = 0;
		
		// Fetch the current user
		ParseUser user = ParseUser.getCurrentUser();
		
		// TODO: Figure out how to get the user name out of the callback.
		Session session = ParseFacebookUtils.getSession();
		Request.newMeRequest(session, new GraphUserCallback() {
			public void onCompleted(GraphUser user, Response response) {
				if (response.getError() != null) {
					//name = user.getName();
				} else { // TODO: Make this more robust
					System.err.println("Error connecting to Facebook");
				}
			}
		});
		name = user.getUsername();
		
		// Get the number of pins they've viewed
		ParseRelation<ParsePin> viewedRelation = user.getRelation("viewed");
		try {
			viewedNum = viewedRelation.getQuery().count();
		} catch (ParseException e) {
			// Error fetching viewed
			return null;
		}
		
		// Get the number of pins they've posted
		ParseRelation<ParsePin> postedRelation = user.getRelation("posted");
		try {
			postedNum = postedRelation.getQuery().count();
		} catch (ParseException e) {
			// Error fetching posted
			return null;
		}
		
		return new User(viewedNum, postedNum, name);
	}
}
