package edu.washington.geopost.test;

import org.junit.After;
import org.junit.Before;

import android.annotation.SuppressLint;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.Button;

import com.robotium.solo.Solo;

import edu.washington.geopost.MainActivity;
import edu.washington.geopost.PostFragment;

@SuppressLint("NewApi")
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private Solo solo;
	private static final String TAG = "MainActivityTest";
	private ActivityMonitor am;
	private LocationManager lm;
	
	private static final String PROVIDER = LocationManager.GPS_PROVIDER;
	private static final double LAT = 37.377166;
	private static final double LNG = -122.086966;
	private static final float ACCURACY = 3.0f;
	
	public MainActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
		solo = new Solo(getInstrumentation(), activity);
		lm = (LocationManager) activity.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		lm.addTestProvider(PROVIDER, false, false, false, false, false, false, false, 0, 5);
		lm.setTestProviderEnabled(PROVIDER, true);
		lm.setTestProviderLocation(PROVIDER, createLocation(LAT, LNG, ACCURACY, PROVIDER));
		Log.d(TAG, "Set it up set it up");
	}
	
	@Before
	protected void setUpMapMocking() {

	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPreconditions() {
		assertNotNull("activity is null", activity);
	}
	
	public void testPostButtonMessage() {
		final String expected = 
				activity.getString(edu.washington.geopost.R.string.button_message);
		Button postButton = (Button) activity.findViewById(edu.washington.geopost.R.id.post_button);
		final String actual = postButton.getText().toString();
		assertEquals(expected, actual);
	}
	
	public void testPostButtonClickOpensFragment() {
		//solo.waitForActivity(MainActivity.class.getName());
		solo.clickOnText("Post");
		solo.clickOnText("Cancel");
	}
	
	public void testMockPost() {
		//solo.waitForActivity(MainActivity.class.getName());
		solo.clickOnText("Post");
		solo.clickOnEditText(0);
		solo.enterText(0, "Test Post. Please Ignore Param 2");
		solo.clickOnText("Post");
	    
	}

	/*
	 * From input arguments, create a single Location with provider set to
	 * "flp"
	 */
	@SuppressLint("NewApi")
	public Location createLocation(double lat, double lng, float accuracy, String provider) {
	    // Create a new Location
	    Location newLocation = new Location(provider);
	    newLocation.setLatitude(lat);
	    newLocation.setLongitude(lng);
	    newLocation.setAccuracy(accuracy);
	    newLocation.setTime(System.currentTimeMillis());
	    newLocation.setElapsedRealtimeNanos(100000);
	    return newLocation;
	}
}
