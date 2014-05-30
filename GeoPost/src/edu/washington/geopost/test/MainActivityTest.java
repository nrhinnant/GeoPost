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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.parse.ParseUser;
import com.robotium.solo.Solo;

import edu.washington.geopost.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private Solo solo;
	private static final String TAG = "MainActivityTest";
	//The below integers represent the position of the 
	//corresponding option in the pin sorting spinner.
	//These values can be found in res/values/stringarrays.xml
	private static final int ALL_POSTS = 0;
	private static final int VIEWED_POSTS = 2;
	private static final int LOCKED_POSTS = 3;
	private static final int MY_POSTS = 4;
	private static final int FRIENDS_POSTS = 1;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		solo = new Solo(getInstrumentation(), activity);
		
		assertFalse(activity.isIncludeViewed());
		assertFalse(activity.isIncludeLocked());
		assertFalse(activity.isIncludePosted());
		assertFalse(activity.isIncludeFriends());
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
	public void testAllPostsSelected() {
		View view = solo.getView(Spinner.class, ALL_POSTS);
		solo.clickOnView(view);
		assertTrue(activity.isIncludeViewed());
		assertTrue(activity.isIncludeLocked());
		assertTrue(activity.isIncludePosted());
		assertTrue(activity.isIncludeFriends());
	}
	
	@Test
	public void testViewedPostsSelected() {
		View view = solo.getView(Spinner.class, VIEWED_POSTS);
		solo.clickOnView(view);
		assertTrue(activity.isIncludeViewed());
		assertTrue(activity.isIncludeLocked());
		assertTrue(activity.isIncludePosted());
		assertTrue(activity.isIncludeFriends());
	}
	
	@Test 
	public void testLockedPostsSelected() {
		View view = solo.getView(Spinner.class, LOCKED_POSTS);
		solo.clickOnView(view);
		assertFalse(activity.isIncludeViewed());
		assertTrue(activity.isIncludeLocked());
		assertFalse(activity.isIncludePosted());
		assertFalse(activity.isIncludeFriends());
	}
	
	@Test
	public void testMyPostsSelected() {
		View view = solo.getView(Spinner.class, MY_POSTS);
		solo.clickOnView(view);
		assertFalse(activity.isIncludeViewed());
		assertFalse(activity.isIncludeLocked());
		assertTrue(activity.isIncludePosted());
		assertFalse(activity.isIncludeFriends());
	}
	
	@Test
	public void testFriendsPostsSelected() {
		View view = solo.getView(Spinner.class, FRIENDS_POSTS);
		solo.clickOnView(view);
		assertTrue(activity.isIncludeViewed());
		assertTrue(activity.isIncludeLocked());
		assertTrue(activity.isIncludePosted());
		assertFalse(activity.isIncludeFriends());
	}
	
	@Test
	public void testPostButtonClickOpensFragment() {
		//solo.waitForActivity(MainActivity.class.getName());
		solo.clickOnText("Post");
		solo.clickOnText("Cancel");
	}
	@Test
	public void testMockPost() {
		//solo.waitForActivity(MainActivity.class.getName());
		solo.clickOnText("Post");
		solo.clickOnEditText(0);
		solo.enterText(0, "Test Post. Please Ignore Param 2");
		solo.clickOnText("Post");
	}
	

}
