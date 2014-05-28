package edu.washington.geopost.test;

import org.junit.Test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import edu.washington.geopost.MainActivity;
import edu.washington.geopost.ProfileActivity;

public class ProfileActivityTest extends ActivityInstrumentationTestCase2<ProfileActivity> {
	private ProfileActivity activity;
	
	public ProfileActivityTest() {
		super(ProfileActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent();
		intent.putExtra("edu.washington.geopost.USERNAME", "Test Name");
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
	public void testUsernameDisplayedMatchesActualUsername() {
		final String expected = 
				activity.getIntent().getStringExtra("edu.washington.geopost.USERNAME");
		TextView nameView = (TextView) activity.findViewById(edu.washington.geopost.R.id.username);
		final String actual = nameView.getText().toString();
		assertEquals(expected, actual);
	}
}
