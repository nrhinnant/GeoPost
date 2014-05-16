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
	 * @return The created pin, or null
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
		/*if (this.isNetworkConnected())
			dbPin.saveInBackground();
		else
			dbPin.saveEventually();
		*/
		
		try {
			dbPin.save();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.d("PostPin", "ParseException with ParseObject.save()");
		}
		
		Pin newPin = new Pin(false, coord, user.getUsername(), dbPin.getObjectId(), message);
		return newPin;
	}
	
	/**
	 * Marks the given pin as being unlocked by the given user and saves the
	 * information to the database.
	 * @param pin The pin that the user is trying to unlock
	 * @return True if the pin was unlocked successfully, false otherwise
	 */
	public boolean unlockPin(Pin pin) {
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
	
	/**
	 * Determines whether the application has internet connectivity
	 * 
	 * @return true if connected, false otherwise
	 */
	private boolean isNetworkConnected() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
		if (activeNetworkInfo == null)
			return false;
		return activeNetworkInfo.isConnected();
	}
}
