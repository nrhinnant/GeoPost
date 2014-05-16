package edu.washington.geopost.test;

import org.junit.Test;

import android.app.Activity;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import edu.washington.geopost.LoginActivity;
import edu.washington.geopost.MainActivity;

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
		solo.clickOnText("Log In");
		Activity fbLogin = getInstrumentation().waitForMonitorWithTimeout(am, 5000);		
		assertNotNull(fbLogin);
		fbLogin.finish();
	}
}
