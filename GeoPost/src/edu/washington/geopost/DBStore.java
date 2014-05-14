package edu.washington.geopost;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * 
 * DBStore sends information to the Parse database using parameterized queries.
 * 
 * @author Andrew Repp
 * 
 */

public class DBStore extends FragmentActivity {
	/**
	 * Adds the given pin to the database.
	 * @param pin The pin that the user is posting that should be in the
	 *            database
	 * @return True if the pin was stored in the database successfully, false
	 *         otherwise
	 */
	public Pin postPin(LatLng coord, String message) {
		// Make the ParsePin to save to the database and set its fields
		// TODO: Write ParsePin constructor to do this?
		ParsePin dbPin = new ParsePin();
		
		ParseUser user = ParseUser.getCurrentUser();
		dbPin.setUser(user);
		
		LatLng pinLocation = coord;
		ParseGeoPoint location = new ParseGeoPoint(pinLocation.latitude,
												   pinLocation.longitude);
		dbPin.setLocation(location);
		
		dbPin.setMessage(message);
		
		// Save the ParsePin to the database
		dbPin.saveEventually();
		
		Pin newPin = new Pin(false, coord, user.getUsername(), dbPin.getObjectId(), message);
		return newPin;
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
		} catch (ParseException e) {
			// Error fetching pin
			return false;
		}
		
		// If we successfully got the ParsePin, get the current user, add the
		// ParsePin to that user's set of viewed pins, and save the updated
		// state to the database.
		if (dbPin != null) {
			ParseUser user = ParseUser.getCurrentUser();
			ParseRelation<ParsePin> viewedPins = user.getRelation("viewed");
			viewedPins.add(dbPin);
			user.saveEventually();
		}
		return success;
	}
}
