package edu.washington.geopost.test;

import org.junit.Test;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import edu.washington.geopost.PostFragment;
import edu.washington.geopost.MainActivity;

public class PostFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private DialogFragment fragment;
	
	public PostFragmentTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		fragment = new PostFragment();
		fragment.show(activity.getSupportFragmentManager(), "post");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testPreconditions() {
		assertNotNull("activity is null", activity);
		assertNotNull("fragment is null", fragment);
	}
	
	@Test
	public void testFragmentIsBeingShown() {
		getInstrumentation().waitForIdleSync();
		Fragment postFragment = 
				activity.getSupportFragmentManager().findFragmentByTag("post");
		assertTrue(postFragment instanceof DialogFragment);
		assertTrue(((DialogFragment) postFragment).getShowsDialog());
	}
}