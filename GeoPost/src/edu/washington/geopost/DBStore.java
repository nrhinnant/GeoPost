package edu.washington.geopost;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * DBStore sends information to the Parse database using parameterized queries.
 * 
 * @authors Neil Hinnant, Andrew Repp
 */

public class DBStore {
	/**
	 * Creates and adds a new pin to the pin database
	 * @param coord The new pin's coordinates. Must not be null
	 * @param message The new pin's message. Must not be null
	 * @param photo The new pin's photo. Null if the pin doesn't have a photo
	 * @return The created pin, or null if updating the database failed or the
	 *         coordinates or message is null
	 */
	public Pin postPin(LatLng coord, String message, Bitmap photo) {
		// Check the message and coordinates are valid
		if (message == null || coord == null) {
			return null;
		}
		
		// Make the ParsePin to save to the database and set its fields
		ParsePin dbPin = new ParsePin();
		ParseUser user = ParseUser.getCurrentUser();
		dbPin.setUser(user);
		ParseGeoPoint location = new ParseGeoPoint(coord.latitude,
												   coord.longitude);
		dbPin.setLocation(location);
		dbPin.setMessage(message);
		
		// Check if we have a photo. If we do, try to save it to the database
		// and update the pin to have this photo.
		Log.d("PostPin", "Before photo");
		if (photo != null) {
	    	Log.d("PHOTO", "before scaling");
	    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	    	byte[] scaledPhoto = bos.toByteArray();
	    	Log.d("PHOTO", "after scaling");
	    	ParseFile photoFile = new ParseFile("pinPhoto.jpg", scaledPhoto);
	    	try {
				photoFile.save();
		    	Log.d("PostPin", "Successfully finished photo save");
		    	dbPin.setPhoto(photoFile);
			} catch (ParseException e) { // Error saving photo
				Log.d("PostPin", "ParseException with ParseFile.save()");
				return null;
			}
		}
		Log.d("PostPin", "After photo");
		
		// Attempt to save the pin to the database. If there's an error, return
		// null.
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
							 user.getString("facebookID"), dbPin.getObjectId(),
							 message, photo);
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
		
		// Because we successfully got the ParsePin, get the current user, add
		// the ParsePin to that user's set of viewed pins, and save the updated
		// state to the database.
		ParseUser user = ParseUser.getCurrentUser();
		ParseRelation<ParsePin> viewedPins = user.getRelation("viewed");
		viewedPins.add(dbPin);
		user.saveEventually();
		
		return createUnlockedPin(pin);
	}
	
	/**
	 * Returns an unlocked version of the given Pin.
	 * @param p The pin to copy
	 * @return A copy of p marked as unlocked
	 */
	private Pin createUnlockedPin(Pin p) {
		return new Pin(false, p.getLocation(), p.getUser(), p.getFacebookID(),
					   p.getPinId(), p.getMessage(), p.getPhoto());
	}
}
