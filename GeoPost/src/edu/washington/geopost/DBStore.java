package edu.washington.geopost;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
 * @author Andrew Repp
 * @author Neil Hinnant
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
		
		// Save the ParsePin to the database
		/*if (this.isNetworkConnected())
			dbPin.saveInBackground();
		else
			dbPin.saveEventually();
		*/
		
		try {
			dbPin.save();
		} catch (ParseException e) { // TODO: Make this more robust
			Log.d("PostPin", "ParseException with ParseObject.save()");
			return null;
		}
		ParseRelation<ParsePin> viewedPins = user.getRelation("viewed");
		viewedPins.add(dbPin);
		ParseRelation<ParsePin> postedPins = user.getRelation("posted");
		postedPins.add(dbPin);
		user.saveEventually();
		
		Pin newPin = new Pin(false, coord, user.getUsername(), dbPin.getObjectId(), message);
		return newPin;
	}
	
	/**
	 * Marks the given pin as being unlocked by the given user and saves the
	 * information to the database.
	 * @param pin The pin that the user is trying to unlock
	 * @return True if the pin was unlocked successfully, false otherwise
	 */
	public Pin unlockPin(Pin pin) {
		boolean success = true;
		
		// Get the ParsePin corresponding to the given Pin.
		ParseQuery<ParsePin> query = ParseQuery.getQuery(ParsePin.class);
		ParsePin dbPin = null;
		try {
			dbPin = query.get(pin.getPinId());
		} catch (Exception e) {
			Log.d("unlockPin", "exception");
			// Error fetching pin
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
	 * Take in a pin and return an unlocked version of it
	 * @param p, the pin to copy
	 * @return a copy of p excepted locked is false
	 */
	private Pin createUnlockedPin(Pin p){
		return new Pin(false, p.getCoord(), p.getUser(), p.getPinId(), p.getMessage());
	}
	
	/**
	 * Determines whether the application has internet connectivity
	 * 
	 * @return true if connected, false otherwise
	 */
	private boolean isNetworkConnected() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			return false;
		} else {
			return activeNetworkInfo.isConnected();
		}
	}
}
