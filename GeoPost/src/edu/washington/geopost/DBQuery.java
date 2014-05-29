package edu.washington.geopost;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;


/**
 * DBQuery retrieves information from the Parse database using parameterized
 * queries.
 * 
 * @authors Katie Madonna, Andrew Repp
 */

public class DBQuery {
	/**
	 * Returns all of the pins located within the geographical box bounded by
	 * the given corners.
	 * @param southWest The lower left corner of the box for which pins are
	 *                  desired
	 * @param northEast The upper right corner of the box for which pins are
	 *                  desired
	 * @return A set of the Pins within the given box, or null if there is an
	 *         error fetching data
	 */
	public Set<Pin> getPins(LatLng southWest, LatLng northEast) {
		// Convert the given Locations to ParseGeoPoints for the query
		ParseGeoPoint sw = new ParseGeoPoint(southWest.latitude, 
											 southWest.longitude);
		ParseGeoPoint ne = new ParseGeoPoint(northEast.latitude,
											 northEast.longitude);
		
		// Set up the ParseQuery for the pins within the given coordinates
		ParseQuery<ParsePin> pinQuery = ParseQuery.getQuery(ParsePin.class);
		pinQuery.include("user");
		pinQuery.whereWithinGeoBox("location", sw, ne);
		
		// Get the results of the query
		List<ParsePin> queryResults = null;
		try {
			queryResults = pinQuery.find();
		} catch (ParseException e) { // Fetching pins had an error
			return null;
		}
		
		// If we reached this point, then we know that we successfully
		// retrieved the pin data from the database. Therefore, we fetch the
		// current user and set up the query for the list of pins they've
		// viewed inside the bounding box.
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null) {
			return new HashSet<Pin>();
		}
		ParseRelation<ParsePin> viewedRelation = user.getRelation("viewed");
		ParseQuery<ParsePin> viewedQuery = viewedRelation.getQuery();
		viewedQuery.whereWithinGeoBox("location", sw, ne);
		
		// Execute the query
		List<ParsePin> viewed = null;
		try {
			viewed = viewedQuery.find();
		} catch (ParseException e) { // Fetching viewed had an error
			return null;
		}
		
		// If we reached this point, then we know that we successfully
		// retrieved all of the pin data from the database. Therefore, we
		// process the data to build up our final result.
		Log.d("getPins", "All pins: " + queryResults);
		Log.d("getPins", "My pins : " + viewed);
		
		// The set that we return
		Set<Pin> pinsToDisplay = new HashSet<Pin>();
		
		// Go over each ParsePin in the result and convert it into an
		// appropriate Pin object for the map.
		for (ParsePin pin : queryResults) {
			// If this pin is in the user's list of viewed pins, then we'll
			// mark the pin as unlocked. Otherwise, we'll set it as locked.
			boolean locked = !viewed.contains(pin);
			Log.d("getPins", pin + " " + locked);

			// Set up the pin's location.
			LatLng location = new LatLng(pin.getLocation().getLatitude(),
										 pin.getLocation().getLongitude());
			
			// Get the pin's original poster and their Facebook ID
			String poster = pin.getUser().getUsername();
			String facebookID = pin.getUser().getString("facebookID");
			
			// Get the pin's photo if it has one
			ParseFile photoFile = pin.getParseFile("photo");
			Bitmap photo = null;
			if (photoFile != null) {
				byte[] photoData;
				try {
					photoData = photoFile.getData();
					photo = BitmapFactory.decodeByteArray(photoData, 0,
														  photoData.length);
				} catch (ParseException e) {
					// There was an error reading the ParseFile, so we can't
					// include the photo in the returned Pin. We'll just leave
					// it as null.
					Log.d("PHOTO", "Couldn't read data from ParseFile");
				}
			}
		
	        // Make the new Pin and add it to the result set
			Pin newPin = new Pin(locked, location, poster, facebookID,
								 pin.getObjectId(), pin.getMessage(), photo);

			pinsToDisplay.add(newPin);
		}
		
		return pinsToDisplay;
	}
	
	/**
	 * Returns a User object containing information about the current user of
	 * the app.
	 * @return The User about which information is desired, or null if there is
	 *         an error fetching the data
	 */
	public User getCurrentUser() {
		String name = null;
		String facebookID = null;
		int viewedNum = 0;
		int postedNum = 0;
		
		// Fetch the current user's name and FacebookID
		ParseUser user = ParseUser.getCurrentUser();
		if (user == null) {
			return null;
		}
		name = user.getUsername();
		facebookID = user.getString("facebookID");
		
		// Get the number of pins they've viewed
		ParseRelation<ParsePin> viewedRelation = user.getRelation("viewed");
		try {
			viewedNum = viewedRelation.getQuery().count();
		} catch (ParseException e) { // Error fetching viewed
			return null;
		}
		
		// Get the number of pins they've posted
		ParseRelation<ParsePin> postedRelation = user.getRelation("posted");
		try {
			postedNum = postedRelation.getQuery().count();
		} catch (ParseException e) { // Error fetching posted
			return null;
		}
		
		return new User(viewedNum, postedNum, name, facebookID);
	}
	

	/**
	 * Returns a set containing the Facebook IDs of the current user's
	 * friends that are signed up for GeoPost.
	 * 
	 * @return A Set<String> containing the Facebook IDs of the current user's
	 *         friends that are signed up for GeoPost, or null if there is an
	 *         error fetching the data
	 */
	public Set<String> getFriends() {
		Set<String> friendIDs = new HashSet<String>();
		
		// Get a list of the ParseUsers with which the current user is friends
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			return friendIDs;
		}
		ParseRelation<ParseUser> friendsRelation = 
				currentUser.getRelation("friends");
		ParseQuery<ParseUser> friendsQuery = friendsRelation.getQuery();
		
		List<ParseUser> friends = null;
		try {
			friends = friendsQuery.find();
		} catch (ParseException e) { // Error fetching the user's friends
			return null;
		}
		
		// fill, and return the Set of Facebook IDs
		for (ParseUser friend : friends) {
			friendIDs.add(friend.getString("facebookID"));
		}
		
		return friendIDs;
	}
}
