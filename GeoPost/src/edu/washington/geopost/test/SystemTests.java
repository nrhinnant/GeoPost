package edu.washington.geopost.test;

import java.util.Collection;

import org.junit.Test;

import android.app.ActivityManager;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.robotium.solo.By;
import com.robotium.solo.Solo;

import edu.washington.geopost.LoginActivity;
import edu.washington.geopost.MainActivity;

public class SystemTests  extends ActivityInstrumentationTestCase2<LoginActivity>  {
	private Solo solo;
	ActivityMonitor am;
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
		am = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	/* This test will run properly if and only if the following conditions are met
	 * 
	 * 		There is a location provider running on the phone
	 * 
	 */
	public void testUseCaseLogInAndDrop() {
		solo.waitForActivity(edu.washington.geopost.LoginActivity.class);
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser == null) || !ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the map activity
			solo.clickOnText("Log In");
			if (getInstrumentation().waitForMonitorWithTimeout(am,  5000) == null) {
				solo.typeTextInWebElement(By.name("email"), TEST_USER);
				solo.typeTextInWebElement(By.name("pass"), TEST_USER_PASSWORD);
				solo.clickOnWebElement(By.textContent("Log In"));
				solo.sleep(5000);
				solo.clickOnWebElement(By.textContent("OK"));
				Log.d("geopost Systemtest", "Logged in as test user");		
			}		
		}
		
		
		MainActivity nextActivity = (MainActivity) getInstrumentation().waitForMonitorWithTimeout(am, 5000);
		assert(nextActivity != null);
		
		solo.clickOnButton("Post");
		solo.enterText(0, TEST_POST_CONTENT);
		solo.clickOnButton("Post");
		solo.sleep(5000);
		ParseUser.logOut();
	}

}