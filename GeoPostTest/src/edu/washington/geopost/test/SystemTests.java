package edu.washington.geopost.test;

import org.junit.Test;

import com.robotium.solo.Solo;

import android.content.Context;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import edu.washington.geopost.LoginActivity;
import edu.washington.geopost.MainActivity;

public class SystemTests  extends ActivityInstrumentationTestCase2<LoginActivity>  {
	private Solo solo;
	String TEST_USER = "open_zjzusdi_user@tfbnw.net";
	String TEST_USER_PASSWORD = "TestUserPass";
	String TEST_POST_CONTENT = "TestPost from Systemtest";
	int BIG_TIMEOUT = 20000000;
	
	public SystemTests() {
		super(LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	/* This test will run properly if and only if the following conditions are met
	 * 
	 * 		There is a user logged in to facebook
	 * 		There is a location provider running on the phone
	 * 
	 * I just learned that Robotium cannot interact with the facebook login webview. 
	 * We thus can't autologin users for facebook. This is a huge setback for me because
	 * it means a true system test that logs into facebook etc is more or less impossible
	 * without a more robust framework. Regardless...
	 */
	public void testUseCaseLogInAndDrop() {
		solo.waitForActivity(edu.washington.geopost.LoginActivity.class);
		solo.clickOnText("Log In");
		if (solo.waitForActivity(MainActivity.class, BIG_TIMEOUT)) {
			solo.waitForText("Post");
			solo.clickOnText("Post");
			solo.enterText(0, TEST_POST_CONTENT);
			solo.clickOnText("Post");
			return;
		}

		
		fail();
	}

}
