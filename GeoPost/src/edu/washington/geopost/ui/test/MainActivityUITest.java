package edu.washington.geopost.ui.test;

import org.junit.Before;
import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;

import com.parse.ParseUser;
import com.robotium.solo.Solo;

import edu.washington.geopost.MainActivity;

public class MainActivityUITest extends ActivityInstrumentationTestCase2<MainActivity> {
	int SHORT_TIMEOUT = 5000;
	private Solo solo;
	public MainActivityUITest() {
		super(MainActivity.class);
	}
	
	public MainActivityUITest(Class<MainActivity> activityClass) {
		super(activityClass);
	}
	
	@Before
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Test
	public void testPostActivityLaunch() {
		boolean postActivityResult = false;
		if (ParseUser.getCurrentUser() == null) {
			solo.clickOnButton("Post");
			postActivityResult = solo.waitForLogMessage("Post Clicked");
			assertTrue(postActivityResult);
		} else {
			fail();
		}
	}
}