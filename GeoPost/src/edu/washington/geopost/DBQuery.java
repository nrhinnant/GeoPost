package edu.washington.geopost;

import java.util.Set;

import android.location.Location;

public class DBQuery {
	/**
	 * 
	 * @param southWest The lower left corner of the user's screen on the map
	 * @param northEast The upper right corner of the user's screen on the map
	 * @return A set of the pins within the given box made with passed coordinates
	 */
	public Set<Pin> getPins(Location southWest, Location northEast) {
		return null;
	}
	
	/**
	 * 
	 * @param userId The user ID of the user we want information about
	 * @return The User that we want information about
	 */
	public User getUser(String userId) {
		return null;
	}
}
