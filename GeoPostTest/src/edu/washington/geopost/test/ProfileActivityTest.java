package edu.washington.geopost.test;

import org.junit.Test;

import com.facebook.widget.ProfilePictureView;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import edu.washington.geopost.MainActivity;
import edu.washington.geopost.ProfileActivity;

public class ProfileActivityTest extends ActivityInstrumentationTestCase2<ProfileActivity> {
	private ProfileActivity activity;
	private static final String USERNAME = "Test Name";
	private static final int NUM_POSTED = 1;
	private static final int NUM_VIEWED = 2;
	private static final String FACEBOOK_ID = "test_facebook_id";
	
	public ProfileActivityTest() {
		super(ProfileActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent();
		intent.putExtra("edu.washington.geopost.USERNAME", USERNAME);
		intent.putExtra("edu.washington.geopost.NUM_POSTED", NUM_POSTED);
    	intent.putExtra("edu.washington.geopost.NUM_VIEWED", NUM_VIEWED);
    	intent.putExtra("edu.washington.geopost.FACEBOOKID", FACEBOOK_ID);
		setActivityIntent(intent);
		activity = getActivity();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testPreconditions() {
		assertNotNull("activity is null", activity);
	}

	@Test
	public void testDisplayedUsernameNotNull() {
		TextView nameView = 
				(TextView) activity.findViewById(edu.washington.geopost.R.id.username);
		final String username = nameView.getText().toString();
		assertNotNull(username);
	}
	
	@Test
	public void testUsernameDisplayedMatchesActualUsername() {
		final String expected = 
				activity.getIntent().getStringExtra("edu.washington.geopost.USERNAME");
		TextView nameView = 
				(TextView) activity.findViewById(edu.washington.geopost.R.id.username);
		final String actual = nameView.getText().toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDisplayedNumPostedNotNull() {
		TextView numPostedView = 
				(TextView) activity.findViewById(edu.washington.geopost.R.id.num_posted);
		final String numPosted = numPostedView.getText().toString();
		assertNotNull(numPosted);
	}
	
	@Test
	public void testNumPostedDisplayedMatchesActualNumPosted() {
		final int expectedNum = 
				activity.getIntent().getIntExtra("edu.washington.geopost.NUM_POSTED", 0);
		TextView numPostedView = 
				(TextView) activity.findViewById(edu.washington.geopost.R.id.num_posted);
		final String actual = numPostedView.getText().toString();
		assertEquals("Posted: " + expectedNum , actual);
	}
	
	@Test
	public void testDisplayedNumViewNotNull() {
		TextView numViewedView = 
				(TextView) activity.findViewById(edu.washington.geopost.R.id.num_viewed);
		final String numViewed = numViewedView.getText().toString();
		assertNotNull(numViewed);
	}
	
	@Test
	public void testNumViewedDisplayedMatchesActualNumViewed() {
		final int expectedNum = 
				activity.getIntent().getIntExtra("edu.washington.geopost.NUM_VIEWED", 0);
		TextView numViewedView = 
				(TextView) activity.findViewById(edu.washington.geopost.R.id.num_viewed);
		final String actual = numViewedView.getText().toString();
		assertEquals("Unlocked: " + expectedNum , actual);
	}
	
	@Test
	public void testProfilePicNotNull() {
		ProfilePictureView p = (ProfilePictureView) 
				activity.findViewById(edu.washington.geopost.R.id.userProfilePicture);
		assertNotNull(p);
	}
	
	@Test
	public void testProfilePicIdMatchesFacebookId() {
		final String expected = 
				activity.getIntent().getStringExtra("edu.washington.geopost.FACEBOOKID");
		ProfilePictureView p = (ProfilePictureView) 
				activity.findViewById(edu.washington.geopost.R.id.userProfilePicture);
		final String actual = p.getProfileId();
		assertEquals(expected, actual);
	}
}
