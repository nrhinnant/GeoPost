package edu.washington.geopost.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.washington.geopost.User;


public class UserTest {

	User testUser;
	int viewedNum = 5;
	int postedNum = 10;
	String name = "Test Name";
	String facebookID = "Facebook ID";
	
	@Before
	public void setUp() {
		testUser = new User(viewedNum, postedNum, name, facebookID);
	}
	
	@Test
	public void testGetNumViewed() {
		assertSame(viewedNum, testUser.getNumViewed());
	}
	
	@Test
	public void testGetNumPosted() {
		assertSame(postedNum, testUser.getNumPosted());
	}
	
	@Test
	public void testGetName() {
		assertEquals(name, testUser.getName());
	}
	
	@Test
	public void testGetFacebookID() {
		assertEquals(facebookID, testUser.getFacebookID());
	}
	
}