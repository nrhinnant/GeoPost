package edu.washington.geopost.test;

import org.junit.Test;

import edu.washington.geopost.MainActivity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity activity;
	
	public MainActivityTest() {
		super(MainActivity.class);
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
		Button postButton = (Button) activity.findViewById(edu.washington.geopost.R.id.post_button);
		activity.onPostButtonClick(postButton);
		getInstrumentation().waitForIdleSync();
		Fragment dialog = getActivity().getSupportFragmentManager().findFragmentByTag("post");
		assertTrue(dialog instanceof DialogFragment);
		assertTrue(((DialogFragment) dialog).getShowsDialog());
	}
}
