package edu.washington.geopost.test;

import org.junit.Test;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.washington.geopost.HelpFragment;
import edu.washington.geopost.MainActivity;

public class HelpFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private DialogFragment fragment;
	
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
	
	@Test
	public void testFragmentIsBeingShown() {
		getInstrumentation().waitForIdleSync();
		Fragment helpFragment = 
				activity.getSupportFragmentManager().findFragmentByTag("help");
		assertTrue(helpFragment instanceof DialogFragment);
		assertTrue(((DialogFragment) helpFragment).getShowsDialog());
	}
}
