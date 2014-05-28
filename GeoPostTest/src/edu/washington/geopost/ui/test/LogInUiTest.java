package edu.washington.geopost.ui.test;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation.ActivityMonitor;
import android.content.ComponentName;
import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.ParseUser;
import com.robotium.solo.Solo;

import edu.washington.geopost.LoginActivity;
import edu.washington.geopost.MainActivity;

public class LogInUiTest extends ActivityInstrumentationTestCase2<LoginActivity> {
	long SHORT_TIMEOUT = 5000;
	private Solo solo;
	private ActivityMonitor facebook_am;
	private ActivityMonitor mainActivity_am;
	private ActivityManager am;
	public LogInUiTest() {
		super(LoginActivity.class);
	}
	
	public LogInUiTest(Class<LoginActivity> activityClass) {
		super(activityClass);
	}
	
	@Before
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		facebook_am = getInstrumentation().addMonitor(com.facebook.LoginActivity.class.getName(), null, false);	
		mainActivity_am = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
		am = (ActivityManager) getInstrumentation().getContext().getSystemService(Context.ACTIVITY_SERVICE);
	}

	@Test
	public void testButtonLaunchesFacebookLogIn() {
		Activity fbLoginResult = null;
		Activity mainActivityLoginResult = null;
		if (ParseUser.getCurrentUser() == null) {
			solo.clickOnButton("Log In");
			fbLoginResult = facebook_am.waitForActivityWithTimeout(SHORT_TIMEOUT);
			mainActivityLoginResult = mainActivity_am.waitForActivityWithTimeout(SHORT_TIMEOUT);
			assertTrue(fbLoginResult != null || mainActivityLoginResult != null);
		} else {
			List<ActivityManager.RunningTaskInfo> allTasks = am.getRunningTasks(1);
			String activityName = allTasks.get(0).topActivity.getClassName();
			assertTrue(activityName.equals(MainActivity.class.getName()));
		}
		ParseUser.logOut();
	}

}
