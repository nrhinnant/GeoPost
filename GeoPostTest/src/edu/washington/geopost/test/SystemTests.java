package edu.washington.geopost.test;

import org.junit.Test;

import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import edu.washington.geopost.LoginActivity;

public class SystemTests  extends ActivityInstrumentationTestCase2<LoginActivity>  {
	private Solo solo;
	String TEST_USER = "open_zjzusdi_user@tfbnw.net";
	String TEST_USER_PASSWORD = "TestUserPass";
	String TEST_POST_CONTENT = "TestPost from Systemtest";
	
	
	
	public SystemTests(Class<LoginActivity> activityClass) {
		super(activityClass);
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Test
	public void testUseCaseLogInAndDrop() {
		solo.clickOnText("Log In");
		solo.assertCurrentActivity("Facebook Login Screen", com.facebook.LoginActivity.class.getCanonicalName());
		solo.clickOnEditText(0);
		solo.enterText(0, TEST_USER);
		solo.enterText(1, TEST_USER_PASSWORD);
		solo.clickOnText("Continue");
		solo.assertCurrentActivity("MainActivity", edu.washington.geopost.MainActivity.class.getCanonicalName());
		solo.clickOnButton(0);
		solo.enterText(0, TEST_POST_CONTENT);
		solo.clickOnText("Post");
		
	}

}
