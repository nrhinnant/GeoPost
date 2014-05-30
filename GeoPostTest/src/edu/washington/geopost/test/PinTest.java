package edu.washington.geopost.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.washington.geopost.Pin;

public class PinTest {
	
	private static final boolean LOCKED = true; 
	private static final String USERNAME = "Test Name"; 
	private static final String FACEBOOK_ID = "Test Facebook ID";
	private static final String PIN_ID = "Test Pin ID";
	private static final String MESSAGE = "Test Message"; 
	private static final String MESSAGE2 = "Test Message 2";

	private static Pin testPin;
	
	@Before
	public void setUp() {
		testPin = new Pin(LOCKED, null, USERNAME, FACEBOOK_ID, PIN_ID, MESSAGE, null);
	}
	
	@Test
	public void testIsLocked() {
		assertTrue(testPin.isLocked());
	}
	
	@Test
	public void testGetUser() {
		assertEquals(USERNAME, testPin.getUser());
	}
	
	@Test
	public void testGetFacebookID() {
		assertEquals(FACEBOOK_ID, testPin.getFacebookID());
	}
	
	@Test
	public void testGetPinID() {
		assertEquals(PIN_ID, testPin.getPinId());
	}
	
	@Test
	public void testGetMessage() {
		assertEquals(MESSAGE, testPin.getMessage());
	}
	
	@Test
	public void testSetMessage() {
		testPin.setMessage(MESSAGE2);
		assertEquals(MESSAGE2, testPin.getMessage());
	}
}
