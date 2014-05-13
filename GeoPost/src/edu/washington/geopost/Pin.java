package edu.washington.geopost;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

/**
 * 
 * A Pin object represents a post on the map.
 * 
 * @author Megan Drasnin
 * 
 */

public class Pin {
	// Whether or not the pin is locked for the user
	private boolean locked;
	// The coordinate of the pin
	private LatLng coord;
	// The user ID of the pin's author user
	private String userId;
	// The unique ID of this pin
	private String pinId;
	// The message that the pin will display
	private String message;
	
	/**
	 * Creates a new Pin.
	 * @param locked True if the pin is locked.
	 * @param coord The pin's location.
	 * @param userId The user who posted the pin.
	 * @param pinId The pin's ID.
	 * @param message The pin's message.
	 */
	public Pin(boolean locked, LatLng coord, String userId, String pinId,
			String message) {
		this.locked = locked;
		this.coord = coord;
		this.userId = userId;
		this.pinId = pinId;
		this.message = message;
	}

	/**
	 * Returns <tt>true</tt> if the pin is locked.
	 * @return <tt>true</tt> if the pin is locked.
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
	 * @return The pin's location.
	 */
	public LatLng getLocation() {
		return coord;
	}
	
	/**
	 * Sets the pin's location.
	 * @param coord The pin's location.
	 */
	public void setLocation(LatLng coord) {
		this.coord = coord;
	}
	
	/**
	 * Returns the userId of the user who posted the pin.
	 * @return The userId of the user who posted the pin.
	 */
	public String getUser() {
		return userId;
	}
	
	/**
	 * Sets the user who posted the pin.
	 * @param userId The user who posted the pin.
	 */
	public void setUser(String userId) {
		this.userId = userId;
	}
	
	/**
	 * Returns the pin's ID.
	 * @return The pin's ID.
	 */
	public String getPinId() {
		return pinId;
	}
	
	/**
	 * Sets the pin's ID.
	 * @param pinId The pin's ID.
	 */
	public void setPinId(String pinId) {
		this.pinId = pinId;
	}
	
	/**
	 * Returns the pin's message.
	 * @return The pin's message.
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the pin's message.
	 * @param message The pin's message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
