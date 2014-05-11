package edu.washington.geopost;

import android.location.Location;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * DBStore sends information to the Parse database using parameterized queries.
 * @author Andrew Repp
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
		boolean success = true; // right now this is always true
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
	 * 
	 * @param pin The pin that the user is trying to unlock
	 * @param userId The user ID of the user that is trying to unlock the pin
	 * @return True if the pin was unlocked successfully, false otherwise
	 */
	public boolean unlockPin(Pin pin, String userId) {
		return false;
	}
}
