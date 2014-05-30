package edu.washington.geopost.test;

import org.junit.Test;

import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import edu.washington.geopost.PostFragment;
import edu.washington.geopost.MainActivity;

public class PostFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private Fragment fragment;
	
	public PostFragmentTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		DialogFragment tempFragment = new PostFragment();
		tempFragment.show(activity.getSupportFragmentManager(), "post");
		getInstrumentation().waitForIdleSync();
		fragment = activity.getSupportFragmentManager().findFragmentByTag("post");
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
		assertTrue(fragment instanceof DialogFragment);
		assertTrue(((DialogFragment) fragment).getShowsDialog());
	}
	
	@Test
	public void testPositiveButtonNotNull() {
		AlertDialog dialog = (AlertDialog) ((DialogFragment) fragment).getDialog();
		Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		assertNotNull("positive button is null", b);
	}
	
	@Test
	public void testNegativeButtonNotNull() {
		AlertDialog dialog = (AlertDialog) ((DialogFragment) fragment).getDialog();
		Button b = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		assertNotNull("negative button is null", b);
	}
	
	@Test
	public void testPositiveButtonMessage() {
		AlertDialog dialog = (AlertDialog) ((DialogFragment) fragment).getDialog();
		Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
		final String expected = 
				activity.getString(edu.washington.geopost.R.string.button_message);
		final String actual = b.getText().toString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testNegativeButtonMessage() {
		AlertDialog dialog = (AlertDialog) ((DialogFragment) fragment).getDialog();
		Button b = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		final String expected = 
				activity.getString(edu.washington.geopost.R.string.cancel);
		final String actual = b.getText().toString();
		assertEquals(expected, actual);
	}
}