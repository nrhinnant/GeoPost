package edu.washington.geopost;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

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
 * @authors Neil Hinnant, Andrew Repp
 * 
 */

public class DBStore extends FragmentActivity {
	/**
	 * Creates and adds a new pin to the pin DB
	 * @param coord The coordinates for this new pin
	 * @param message The pin's message
	 * @return The created pin, or null if updating the DB failed.
	 */
	public Pin postPin(LatLng coord, String message) {
		if (message == null) {
			return null;
		}
		
		// Make the ParsePin to save to the database and set its fields\
		ParsePin dbPin = new ParsePin();
		ParseUser user = ParseUser.getCurrentUser();
		dbPin.setUser(user);
		LatLng pinLocation = coord;
		ParseGeoPoint location = new ParseGeoPoint(pinLocation.latitude,
												   pinLocation.longitude);
		dbPin.setLocation(location);
		dbPin.setMessage(message);
		
		try {
			dbPin.save();
		} catch (ParseException e) { // Error saving pin
			Log.d("PostPin", "ParseException with ParseObject.save()");
			return null;
		}
		
		// Update the user's viewed and posted pins to contain the new pin
		ParseRelation<ParsePin> viewedPins = user.getRelation("viewed");
		viewedPins.add(dbPin);
		ParseRelation<ParsePin> postedPins = user.getRelation("posted");
		postedPins.add(dbPin);
		user.saveEventually();
		
		Pin newPin = new Pin(false, coord, user.getUsername(), 
							 user.getString("facebookID"),
							 dbPin.getObjectId(), message);
		return newPin;
	}
	
	/**
	 * Marks the given pin as being unlocked by the given user and saves the
	 * information to the database.
	 * @param pin The pin that the user is trying to unlock
	 * @return A copy of the Pin marked as unlocked if successful, null
	 *         otherwise
	 */
	public Pin unlockPin(Pin pin) {
		// Get the ParsePin corresponding to the given Pin.
		ParseQuery<ParsePin> query = ParseQuery.getQuery(ParsePin.class);
		ParsePin dbPin = null;
		try {
			dbPin = query.get(pin.getPinId());
		} catch (Exception e) { // Error fetching pin
			Log.d("unlockPin", "exception");
			return null;
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
		
		return createUnlockedPin(pin);
	}
	
	/**
	 * Returns an unlocked version of the given Pin.
	 * @param p The pin to copy
	 * @return A copy of p marked as unlocked
	 */
	private Pin createUnlockedPin(Pin p) {
		return new Pin(false, p.getLocation(), p.getUser(), p.getFacebookID(), p.getPinId(), 
					   p.getMessage());
	}
	
	/**
	 * Determines whether the application has internet connectivity
	 * @return true if connected, false otherwise
	 */
	/*
	// We're unsure if we'll need this in the future, so we'll leave it
	// here for now.
	private boolean isNetworkConnected() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			return false;
		} else {
			return activeNetworkInfo.isConnected();
		}
	}
	*/
}
