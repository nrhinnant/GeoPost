package edu.washington.geopost;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * A Pin object represents a post on the map.
 * 
 * @authors Megan Drasnin, Andrew Repp
 * 
 */

public class Pin {
	// Whether or not the pin is locked for the user
	private boolean locked;
	// The coordinate of the pin
	private LatLng coord;
	// The username of the pin's author
	private String username;
	// The unique ID of this pin
	private String pinId;
	// The message that the pin will display
	private String message;
	// The facebook id of the author
	private String facebookId;
	// The bitmap of the photo
	private Bitmap photo;
	
	/**
	 * Creates a new Pin from the given parameters.
	 * @param locked True if the pin is locked
	 * @param coord The pin's location
	 * @param username The username of the user who posted the pin
	 * @param facebookId The Facebook ID of the user who posted the pin
	 * @param pinId The pin's ID
	 * @param message The pin's message
	 * @param photo The photo associated with the pin, or null if there is none
	 */
	public Pin(boolean locked, LatLng coord, String username, String facebookId,
			String pinId, String message, Bitmap photo) {
		this.locked = locked;
		this.coord = coord;
		this.username = username;
		this.facebookId = facebookId;
		this.pinId = pinId;
		this.message = message;
		this.photo = photo;
	}
	
	/**
	 * Creates a new Pin from the given parameters.
	 * @param location The coordinates of the pin
	 * @param pid The pin's id
	 * @param message The text of the pin's message
	 */
	public Pin(LatLng coord, String pinId, String message) {
		this(false, coord, null, null, pinId, message, null);
	}

	/**
	 * Returns <tt>true</tt> if the pin is locked.
	 * @return <tt>true</tt> if the pin is locked
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * Locks the pin.
	 */
	public void lock() {
		locked = true;
	}
	
	/**
	 * Unlocks the pin.
	 */
	public void unlock() {
		locked = false;
	}
	
	/**
	 * Returns the pin's location.
	 * @return The pin's location
	 */
	public LatLng getLocation() {
		return coord;
	}
	
	/**
	 * Sets the pin's location.
	 * @param coord The pin's location
	 */
	public void setLocation(LatLng coord) {
		this.coord = coord;
	}
	
	/**
	 * Returns the username of the user who posted the pin.
	 * @return The username of the user who posted the pin
	 */
	public String getUser() {
		return username;
	}
	
	/**
	 * Sets the user who posted the pin.
	 * @param username The username of the user who posted the pin
	 */
	public void setUser(String username) {
		this.username = username;
	}
	
	/**
	 * Returns the Facebook ID of the user who posted the pin.
	 * @return The Facebook ID of the user who posted the pin
	 */
	public String getFacebookID() {
		return facebookId;
	}
	
	/**
	 * Sets the Facebook ID of the user who posted the pin.
	 * @param facebookId The Facebook ID of the user who posted the pin
	 */
	public void setFacebookID(String facebookId) {
		this.facebookId = facebookId;
	}
	
	/**
	 * Returns the pin's ID.
	 * @return The pin's ID
	 */
	public String getPinId() {
		return pinId;
	}
	
	/**
	 * Sets the pin's ID.
	 * @param pinId The pin's ID
	 */
	public void setPinId(String pinId) {
		this.pinId = pinId;
	}
	
	/**
	 * Returns the pin's message.
	 * @return The pin's message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the pin's message.
	 * @param message The pin's message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Returns the pin's photo or null if the pin doesn't have a photo.
	 * @return The pin's photo, or null if there is none
	 */
	public Bitmap getPhoto() {
		return photo;
	}
	
	/**
	 * Sets the pin's photo.
	 * @param photo The pin's photo
	 */
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	
	/**
	 * Standard hashcode function for pin.
	 * @return int hashcode for the pin
	 */
	@Override
	public int hashCode() {
		return pinId.hashCode();		
	}
	
	/**
	 * Determines whether this Pin equals the given Pin. Two Pins are equal
	 * if and only if they have the same pinID.
	 * @param other object to compare
	 * @return true if this and o are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pin))
			return false;
		Pin p = (Pin) o;
		return pinId.equals(p.pinId);
	}
}
