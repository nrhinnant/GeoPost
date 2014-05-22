package edu.washington.geopost.test;

import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.robotium.solo.By;
import com.robotium.solo.Solo;

import edu.washington.geopost.LoginActivity;
import edu.washington.geopost.MainActivity;

public class LoginActivityTest extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private Solo solo;
	private ActivityMonitor am;
	String TEST_USER = "open_zjzusdi_user@tfbnw.net";
	String TEST_USER_PASSWORD = "TestUserPass";
	
	public LoginActivityTest() {
		super(LoginActivity.class);
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		am = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);	
		ParseUser.logOut();
	}
	
	public void testOnLoginButtonClicked() {
		solo.sleep(5000);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser == null) || !ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the map activity
			solo.clickOnText("Log In");
			// Did it go to the fb login screen or the map?
			if (getInstrumentation().waitForMonitorWithTimeout(am,  5000) == null) {
				solo.typeTextInWebElement(By.name("email"), TEST_USER);
				solo.typeTextInWebElement(By.name("pass"), TEST_USER_PASSWORD);
				solo.clickOnWebElement(By.textContent("Log In"));
				solo.sleep(5000);
				solo.clickOnWebElement(By.textContent("OK"));
				Log.d("geopost Systemtest", "Logged in as test user");		
			}		
		}
		assertNotNull(ParseUser.getCurrentUser());

		getActivity().finish();
		ParseUser.logOut();
	}
}
