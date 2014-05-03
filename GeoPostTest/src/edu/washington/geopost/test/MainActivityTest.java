package edu.washington.geopost.test;

import edu.washington.geopost.MainActivity;
import android.app.Activity;
import android.test.ActivityUnitTestCase;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
	private Activity activity;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		activity = getActivity();
	}
	
	public void sampleTest() {
		assertTrue(false);
	}
}
