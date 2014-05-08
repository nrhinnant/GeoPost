package edu.washington.geopost;

import com.google.android.gms.maps.model.LatLng;

/**
 * This is an immutable pin class to represent a pin posted by a GeoPost user
 * @author davism78
 *
 */
public class GeoPostPin {
	// The coordinates of this post's location
	private LatLng location;
	
	// Unique user id of poster
	
	// Unique pin id
	
	
	
	/**
	 * @return location of post
	 */
	public LatLng getLocation(){
		return location;
	}
}
