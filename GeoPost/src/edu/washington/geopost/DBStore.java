package edu.washington.geopost;

public class DBStore {
	/**
	 * 
	 * @param pin The pin that the user is posting that should be in the database
	 * @return True if the pin was stored in the database successfully, false otherwise
	 */
	public boolean postPin(Pin pin) {
		return false;
	}
	
	/**
	 * 
	 * @param pin The pin that the user is trying to unlock
	 * @param userId The user ID of the user that is trying to unlock the pin
	 * @return True if the pin was unlocked successfully, false otherwise
	 */
	public boolean unlockPin(Pin pin, String userId) {
		return false;
	}
}
