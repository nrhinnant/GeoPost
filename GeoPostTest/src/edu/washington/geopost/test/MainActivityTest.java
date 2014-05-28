package edu.washington.geopost.test;

import org.junit.Before;
import org.junit.Test;

import android.annotation.SuppressLint;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;

import com.parse.ParseUser;
import com.robotium.solo.Solo;

import edu.washington.geopost.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private Solo solo;
	private static final String TAG = "MainActivityTest";
	
	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		solo = new Solo(getInstrumentation(), activity);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testPreconditions() {
		assertNotNull("activity is null", activity);
	}
	
	@Test
	public void testPostButtonMessage() {
		final String expected = 
				activity.getString(edu.washington.geopost.R.string.button_message);
		Button postButton = (Button) activity.findViewById(edu.washington.geopost.R.id.post_button);
		final String actual = postButton.getText().toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPostButtonClickOpensFragment() {
		//solo.waitForActivity(MainActivity.class.getName());
		solo.clickOnText("Post");
		solo.clickOnText("Cancel");
	}
	
	public void testMockPost() {
		//solo.waitForActivity(MainActivity.class.getName());
		solo.clickOnText("Post");
		solo.clickOnEditText(0);
		solo.enterText(0, "Test Post. Please Ignore Param 2");
		solo.clickOnText("Post");
	}
}
