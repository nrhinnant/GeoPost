package edu.washington.geopost;

import android.location.Location;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * 
 * DBStore sends information to the Parse database using parameterized queries.
 * 
 * @author Andrew Repp
 * 
 */

public class DBStore {
	/**
	 * Adds the given pin to the database.
	 * @param pin The pin that the user is posting that should be in the
	 *            database
	 * @return True if the pin was stored in the database successfully, false
	 *         otherwise
	 */
	public boolean postPin(Pin pin) {
		boolean success = true; // TODO: right now this is always true
		
		// Make the ParsePin to save to the database and set its fields
		// TODO: Write ParsePin constructor to do this?
		ParsePin dbPin = new ParsePin();
		
		dbPin.setUser(ParseUser.getCurrentUser());
		
		Location pinLocation = pin.getLocation();
		ParseGeoPoint location = new ParseGeoPoint(pinLocation.getLatitude(),
												   pinLocation.getLongitude());
		dbPin.setLocation(location);
		
		dbPin.setMessage(pin.getMessage());
		
		// Save the ParsePin to the database
		dbPin.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e != null) { // error TODO: Make this more robust
					System.err.println("error saving pin");
				}
			}
		});
		return success;
	}
	
	/**
	 * Marks the given pin as being unlocked by the given user and saves the
	 * information to the database.
	 * @param pin The pin that the user is trying to unlock
	 * @param userId The user ID of the user that is trying to unlock the pin
	 * @return True if the pin was unlocked successfully, false otherwise
	 */
	public boolean unlockPin(Pin pin, String userId) {
		boolean success = true;
		
		// Get the ParsePin corresponding to the given Pin.
		ParseQuery<ParsePin> query = ParseQuery.getQuery("ParsePin");
		ParsePin dbPin = null;
		try {
			dbPin = query.get(pin.getPinId());
		} catch (ParseException e) { // TODO: Make this more robust
			success = false;
			System.err.println("error fetching pin");
		}
		
		// If we successfully got the ParsePin, get the current user, add the
		// ParsePin to that user's set of viewed pins, and save the updated
		// state to the database.
		if (dbPin != null) {
			ParseUser user = ParseUser.getCurrentUser();
			ParseRelation<ParsePin> viewedPins = user.getRelation("viewed");
			viewedPins.add(dbPin);
			user.saveInBackground(new SaveCallback() {
				public void done(ParseException e) {
					if (e != null) { // error TODO: Make this more robust
						System.err.println("err saving relation");
					}
				}
			});
		}
		return success;
	}
}
