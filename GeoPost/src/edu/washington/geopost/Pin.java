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
}
