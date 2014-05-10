package edu.washington.geopost;

import android.location.Location;

public class Pin {
	// Whether or not the pin is locked for the user
	private boolean locked;
	// The coordinate of the pin
	private Location coord;
	// The user ID of the pin's author user
	private String userId;
	// The unique ID of this pin
	private String pinId;
	// The message that the pin will display
	private String message;
	
	
	/**
	 * Creates a new Pin.
	 * @param locked True if the pin is locked.
	 * @param coord The pin's coordinates.
	 * @param userId The userId of the pin's author.
	 * @param pinId The pin's Id.
	 * @param message The message that the pin will display.
	 */
	public Pin(boolean locked, Location coord, String userId, String pinId, String message) {
		this.locked = locked;
		this.coord = coord;
		this.userId = userId;
		this.pinId = pinId;
		this.message = message;
	}

	/**
	 * Returns true if the pin is locked.
	 * @return true if the pin is locked.
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * Returns the pin's Location.
	 * @return The pin's Location.
	 */
	public Location getCoordinates() {
		return coord;
	}
	
	/**
	 * Returns the userId of the pin's Author.
	 * @return The userId of the pin's Author.
	 */
	public String getPinAuthor() {
		return userId;
	}
	
	/**
	 * Returns the pin's ID.
	 * @return The pin's ID.
	 */
	public String getPinId() {
		return pinId;
	}
	
	/**
	 * Returns the message that the pin will display.
	 * @return The message that the pin will display.
	 */
	public String getMessage() {
		return message;
	}
	
	
	/**
	 * Sets the pin's locked status.
	 * @param locked True if the pin is locked. 
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	/**
	 * Sets the pin's location.
	 * @param coord The location of the pin.
	 */
	public void setLocation(Location coord) {
		this.coord = coord;
	}
	
	/**
	 * Sets the author of the pin.
	 * @param userId The author's userId.
	 */
	public void setPinAuthor(String userId) {
		this.userId = userId;
	}
	
	/**
	 * Sets the pin's ID.
	 * @param pinId The pin's ID.
	 */
	public void setPinId(String pinId) {
		this.pinId = pinId;
	}
	
	/**
	 * Sets the message the pin will display.
	 * @param message The message the pin will display.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
