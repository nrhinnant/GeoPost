package edu.washington.geopost;

import com.parse.*;

/**
 * 
 * ParsePin is a subclass of ParseObject and reflects how pin information
 * is stored in the database. The class contains getters and setters for all
 * the information associated with a pin, message, posting user, and location.
 * 
 * @author Katie Madonna
 *
 */

@ParseClassName("Pin")  // associates with a class of ParseObject
public class ParsePin extends ParseObject {
	
	public ParsePin() {
		// Default constructor required
	}
	
	/**
	 * Get for the message of the ParsePin.
	 * @return the string message currently contained by the pin.
	 */
	public String getMessage() {
		return getString("message");
	}
    
	/**
	 * Set the message of the ParsePin.
	 * @param message string to store in the pin.
	 */
	public void setMessage(String message) {
		put("message", message);
	}
    
	/**
	 * Get the user who posted the pin.
	 * @return ParseUser representing the poster of the pin.
	 */
	public ParseUser getUser() {
		return getParseUser("user");
	}
    
	/**
	 * Set the user who posted the pin.
	 * @param user ParseUser representing the poster of the pin.
	 */
	public void setUser(ParseUser user) {
		put("user", user);
	}
    
	/**
	 * Get the location where the pin was posted.
	 * @return ParseGeoPoint location where the pin was posted.
	 */
	public ParseGeoPoint getLocation() {
		return getParseGeoPoint("location");
	}
    
	/**
	 * Sets the location of the ParsePin.
	 * @param location where the pin will be posted.
	 */
	public void setLocation(ParseGeoPoint location) {
		put("location", location);
	}
	
	/**
	 * Standard hashcode function for ParsePin.
	 * @return int hashcode for the ParsePin.
	 */
	@Override
	public int hashCode() {
		return getObjectId().hashCode();		
	}
	
	/**
	 * Determines whether this ParsePin equals the given object. Two ParsePins
	 * are equal if and only if they have the same object id.
	 * @param other object to compare
	 * @return true if this and o are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ParsePin))
			return false;
		ParsePin p = (ParsePin) o;
		return getObjectId().equals(p.getObjectId());
	}
	
	/**
	 * 
	 */
	public boolean equals2(Object o) {
		if (!(o instanceof ParsePin)) 
			return false;
		ParsePin p = (ParsePin) o;
		return p.getString("name").equals(this.getString("name")) &&
			   p.getString("facebookID").equals(this.getString("facebookID")) &&
			   p.getLocation().equals(this.getLocation());
		
	}
	/**
	 * Returns string representation of pin which
	 * is the pin's id
	 */
	@Override
	public String toString() {
		return getObjectId();
	}
}
