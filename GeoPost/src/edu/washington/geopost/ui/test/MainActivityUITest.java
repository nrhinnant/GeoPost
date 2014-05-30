package edu.washington.geopost.ui.test;

import org.junit.Before;
import org.junit.Test;
import android.test.ActivityInstrumentationTestCase2;
import com.parse.ParseUser;
import com.robotium.solo.Solo;
import edu.washington.geopost.LoginActivity;

public class MainActivityUITest extends ActivityInstrumentationTestCase2<LoginActivity> {
	int SHORT_TIMEOUT = 5000;
	private Solo solo;
	public MainActivityUITest() {
		super(LoginActivity.class);
	}
	
	public MainActivityUITest(Class<LoginActivity> activityClass) {
		super(activityClass);
	}
	
	@Before
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Test
	public void testProfileActivityLaunch() {
		boolean postActivityResult = false;
		if (ParseUser.getCurrentUser() == null) {
			solo.clickOnButton("Post");
			postActivityResult = solo.waitForFragmentByTag(edu.washington.geopost.PostFragment.class.getName(), SHORT_TIMEOUT);
			assertTrue(postActivityResult);
		} else {
			fail();
		}
	}
}