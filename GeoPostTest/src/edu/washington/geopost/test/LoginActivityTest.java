package edu.washington.geopost.test;

import java.util.Collection;

import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.ParseUser;
import com.robotium.solo.Solo;
import com.robotium.solo.WebElement;

import edu.washington.geopost.LoginActivity;

public class LoginActivityTest extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private Solo solo;
	private ActivityMonitor am;
	
	public LoginActivityTest() {
		super(LoginActivity.class);
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		am = getInstrumentation().addMonitor(com.facebook.LoginActivity.class.getName(), null, false);	
	}
	
	public void testOnLoginButtonClicked() {
		ParseUser.logOut();
		solo.sleep(5000);
		solo.clickOnText("Log In");
		solo.sleep(5000);
		
		Collection<WebElement> we = solo.getCurrentWebElements();
		assert(we.size() > 0);
		
		getActivity().finish();
		ParseUser.logOut();
	}
}
