package edu.washington.geopost;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;

public class Pin {
	public Pin(LatLng location, String pid, String m){
		coord = location;
		pinId = pid;
		message = m;
	}
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
}
