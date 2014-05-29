package edu.washington.geopost.test;

import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;
import edu.washington.geopost.HelpFragment;
import edu.washington.geopost.MainActivity;

public class HelpFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private HelpFragment fragment;
	
	public HelpFragmentTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		fragment = new HelpFragment();
		fragment.show(activity.getSupportFragmentManager(), "help");
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
}
