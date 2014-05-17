package edu.washington.geopost.test;

import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;
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
		activity = getActivity();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testPreconditions() {
		assertNotNull("activity is null", activity);
	}
}
