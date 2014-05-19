package edu.washington.geopost;

/**
 * 
 * A User object represents a user of the GeoPost app.
 * 
 * @author Megan Drasnin
 * 
 */

public class User {
	// Number of pins the user has viewed
	private int viewedNum;
	// Number of pins the user has posted
	private int postedNum;
	// The name of the user
	private String name;
	// The user's Facebook ID
	private String facebookID;
	
	/**
	 * Creates a new User.
	 * @param viewedNum The number of pins the user has viewed.
	 * @param postedNum The number of pins the user has posted.
	 * @param name The name of the user.
	 */
	public User(int viewedNum, int postedNum, String name, String facebookID) {
		this.viewedNum = viewedNum;
		this.postedNum = postedNum;
		this.name = name;
		this.facebookID = facebookID;
	}
	
	/**
	 * Returns the number of pins the user has viewed.
	 * @return The number of pins the user has viewed.
	 */
	public int getNumViewed() {
		return viewedNum;
	}
	
	/**
	 * Returns the number of pins the user has posted.
	 * @return The number of pins the user has posted.
	 */
	public int getNumPosted() {
		return postedNum;
	}
	
	/**
	 * Returns the user's name.
	 * @return The user's name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return's the user's Facebook ID.
	 * @return The user's Facebook ID.
	 */
	public String getFacebookID() {
		return facebookID;
	}
	
	/**
	 * Sets the number of pins the user has viewed.
	 * @param viewedNum The number of pins the user has viewed.
	 */
	public void setNumViewed(int viewedNum) {
		this.viewedNum = viewedNum;
	}
	
	/**
	 * Sets the number of pins the user has posted.
	 * @param postedNum The number of pins the user has posted.
	 */
	public void setNumPosted(int postedNum) {
		this.postedNum = postedNum;
	}
	
	/**
	 * Sets the user's name.
	 * @param name The user's name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the user's Facebook ID.
	 * @param facebookID The user's Facebook ID.
	 */
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}
}
