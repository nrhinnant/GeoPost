package edu.washington.geopost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * This is the activity of GeoPost that displays the map of pins to the user. It
 * also displays controls to them for posting and filtering pins as well as
 * viewing their profile
 * 
 * @author Matt, Mike, Ethan
 */
public class MainActivity extends FragmentActivity implements
		OnMarkerClickListener,
		PostFragment.PostDialogListener,
		EnableLocationFragment.EnableLocationDialogListener,
		OnItemSelectedListener, OnCameraChangeListener, ConnectionCallbacks,
		OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	// Zoom level upon opening app, 16 is on the order of UW campus size
	// No units are specified, range of zoom is 2.0 to 21.0 (bigger => closer)
	public static final float INIT_ZOOM = 16;
	// Tag for the app
	public static final String TAG = "GeoPost";
	// Thickness of the unlocking circle border line
	public static final float BORDER_THICKNESS = 4;
	// Scale of the unlocking circle in lat/long coord difference
	public static final double RANGE_RADIUS = 0.0015;
	// The meters between two lat/lng lines
	public static final double COORD_IN_METERS = 111319.9;
	// The radius of the earth in meters
	public static final int EARTH_RADIUS = 6366000;
	// The number of milliseconds in a second
	public static final int SEC_TO_MILLIS = 1000;
	// The update interval for location in seconds
	public static final int UPDATE_INTERVAL = 5;
	// The fastest possible update interval in seconds
	public static final int FASTEST_UPDATE = 1;

	// Location related fields
	// The main map that is shown to the user
	private GoogleMap map;
	// Check to see if marker window is open
	private boolean markerWindowShown;
	// The background thread that handles getting pins from database
	private RefreshMapTask refreshThread;
	// The circle drawn on the map
	private Circle unlockedRadius;

	// The location client that handles location
	private LocationClient locationClient;
	// The location request which has parameters about location updates
	private LocationRequest locationRequest;

	// Database interfaces
	private DBQuery dbq;
	private DBStore dbs;

	// Sorting option
	private enum SortingOption {
		ALL, FB_FRIENDS, LOCKED,
		VIEWED, MY_POSTS
	}
	
	SortingOption sortingOption;

	// Current user and their facebook friends
	private User currentUser;
	private Set<String> friends;

	// A map of all pins currently drawn in the app
	private HashMap<Marker, Pin> geoposts;

	// Window for pin being displayed
	private ViewPinWindow vpw;
	
	private ProgressBar loadingWheel;

	/**
	 * Called upon opening of the activity. Initializes all of the UI
	 * components, location, database interfaces, and makes initial call to zoom
	 * in on location and get pins to put on map.
	 * 
	 * @param Bundle
	 *            The saved instance state of the app
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final int result = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (result != ConnectionResult.SUCCESS) {
			Log.d("DEBUG", "MainActivity - Google Play messed up, result = "
					+ result);
			Toast toast = Toast.makeText(this,
					"Google Play service is not available", Toast.LENGTH_LONG);
			toast.show();
			finish();
		}

		locationClient = new LocationClient(this, this, this);
		locationClient.connect();

		setUpMapIfNeeded();

		// Set filters to show all posts
		sortingOption = SortingOption.ALL;

		// Setup collection of markers on map to actual pins
		geoposts = new HashMap<Marker, Pin>();

		// Initialize the database interfaces
		dbq = new DBQuery();
		dbs = new DBStore();
		
		Log.d("LoadWheel", "Before creating");
		loadingWheel = (ProgressBar)findViewById(R.id.progressBar1);
		Log.d("LoadWheel", "findViewById done");
		loadingWheel.setVisibility(View.GONE);
		Log.d("LoadWheel", "Initially gone");
		
		Log.d("DEBUG", "MainActivity - onCreate, just before network stuff");
		
		if (isNetworkAvailable()) {
			new GetUserTask().execute();
			Log.d("DEBUG", "MainActivity - onCreate, grabbed current user");
			new GetFriendsTask().execute();
			Log.d("DEBUG", "MainActivity - onCreate, grabbed friends");
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Network unavailable", Toast.LENGTH_LONG);
			toast.show();
		}
		Log.d("DEBUG",
				"MainActivity - onCreate, successful network setup/handling");
		// Set the pin pop up windows to use the ViewPinWindow class
		vpw = new ViewPinWindow(this);
		if (vpw == null)
			Log.d("LC", "VPW IS NULL");
		if (map == null)
			Log.d("LC", "MAP IS NULL");
		map.setInfoWindowAdapter(vpw);

		markerWindowShown = false;

		Log.d("DEBUG", "MainActivity - before map stuff");
		map.setMyLocationEnabled(true);
		map.setOnMarkerClickListener(this);
		map.setOnCameraChangeListener(this);
		map.getUiSettings().setRotateGesturesEnabled(false);

		Log.d("DEBUG", "MainActivity - after map stuff");

		// Create the Async Task that will be used to refresh
		// pins on the screen
		refreshThread = new RefreshMapTask();
		Log.d("DEBUG", "MainActivity - end of onCreate");
		
		// Check for first time users and start help fragment
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			boolean isNewUser = extras.getBoolean("NewUser");
			if (isNewUser) {
				openHelpFragment();
			}
		}
	}

	/**
	 * 
	 * @author Matt
	 * For this given user, starts a background task that queries
	 * the database for a set of the user's Facebook friends which
	 * we store for using later
	 */
	private class GetFriendsTask extends AsyncTask<Void, Void, Set<String>> {
		@Override
		protected Set<String> doInBackground(Void... params) {
			Log.d("DEBUG", "Friends background start");
			Set<String> results = dbq.getFriends();
			Log.d("DEBUG", "found Friends");
			return results;
		}

		protected void onPostExecute(Set<String> results) {
			Log.d("DEBUG", "Friends background end");
			friends = results;
		}
	}

	/**
	 * 
	 * @author Matt
	 * Starts a background task that will query the database for
	 * information on the current user of this app
	 */
	private class GetUserTask extends AsyncTask<Void, Void, User> {
		@Override
		protected User doInBackground(Void... params) {
			Log.d("DEBUG", "User background start");
			User user = dbq.getCurrentUser();
			Log.d("DEBUG", "got user");
			return user;
		}

		protected void onPostExecute(User user) {
			Log.d("DEBUG", "User background end");
			currentUser = user;
		}
	}

	/**
	 * Called on Activity stop, stop location updates and disconnect location
	 * client.
	 */
	@Override
	protected void onStop() {
		Log.d("OnStop", "Stop Periodic Updates");
		// If the client is connected
		if (locationClient.isConnected()) {
			stopPeriodicUpdates();
		}
		/*
		 * After disconnect() is called, the client is considered "dead".
		 */
		locationClient.disconnect();
		super.onStop();
	}

	/**
	 * Stops the location client from updating location
	 */
	private void stopPeriodicUpdates() {
		locationClient.removeLocationUpdates(this);
	}

	/**
	 * Starts the location client to continually update location
	 */
	private void startPeriodicUpdates() {
		locationClient.requestLocationUpdates(locationRequest, this);
	}

	/**
	 * Sends the app to the top of the background app stack.
	 * User is brought back to main screen of phone.
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	/**
	 * Request updates for location and check map setup
	 */
	@Override
	protected void onResume() {
		super.onResume();
		locationClient.connect();
		setUpMapIfNeeded();
	}

	/**
	 * Remove the locationlistener updates when Activity is paused
	 */
	@Override
	protected void onPause() {
		super.onPause();
		locationClient.disconnect();
	}

	/**
	 * Called when the location client is connected. It sets up the location
	 * request settings, zooms into the current location, and draws the
	 * unlocking radius around the user's current location.
	 * 
	 * @param Bundle of data provided to clients by Google Play services. 
	 * 		  May be null if no content is provided by the service.
	 */
	@Override
	public void onConnected(Bundle arg0) {
		Log.d("DEBUG", "onConnected begin");
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		locationRequest.setInterval(UPDATE_INTERVAL * SEC_TO_MILLIS);
		// Set the fastest update interval to 1 second
		locationRequest.setFastestInterval(FASTEST_UPDATE * SEC_TO_MILLIS);
		// Make the app open up to your current location
		Location currentLocation = locationClient.getLastLocation();
		if (currentLocation != null) {
			LatLng myLatLng = new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude());

			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
					INIT_ZOOM));

			if (unlockedRadius != null) {
				unlockedRadius.remove();
			}

			drawCircle(new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude()));
		} else {
			DialogFragment newFragment = new EnableLocationFragment();
			newFragment.show(getSupportFragmentManager(), "enableLocation");
		}
		Log.d("DEBUG", "onConnected end");
		startPeriodicUpdates();
	}

	/**
	 * Called when the user clicks to enable the GPS on their phone, opens up
	 * settings page on phone where they can make this change
	 * 
	 * @param dialog The DialogFragment that the button was clicked from
	 */
	public void onEnableLocationPositiveClick(DialogFragment dialog) {
		Intent gpsOptionsIntent = new Intent(
				android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	/**
	 * Called whenever the connection to LocationClient fails
	 * @param arg0 Contains information about what kind of connection error occurred
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.d("DEBUG", "onConnectedFailed");
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
	}

	/**
	 * Called whenever the connection to LocationClient is disconnected
	 */
	@Override
	public void onDisconnected() {
		Log.d("DEBUG", "onDisconnected");
		Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
	}

	/**
	 * Add a pin to the map, determines visibility and color details for
	 * specific pin and adds it to main collection as well as draws it on the
	 * map.
	 * 
	 * @param pin the pin to be added
	 */
	private void addPin(Pin pin) {
		if (friends == null) {
			friends = dbq.getFriends();
		}
		float color = BitmapDescriptorFactory.HUE_RED;
		Log.d("addPin", pin.getUser());
		if (currentUser == null) {
			if (!isNetworkAvailable()) {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Network unavailable", Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			currentUser = dbq.getCurrentUser();
		}
		if (wasPostedByUser(pin)) {
			// pin is user's posted pin
			color = BitmapDescriptorFactory.HUE_VIOLET;
		} else if (!pin.isLocked()) {
			// pin is unlocked
			color = (float) 220.0; // blue
		}		

		Marker m = map.addMarker(new MarkerOptions().title(pin.getMessage())
				.snippet(pin.getUser()).position(pin.getLocation())
				.icon(BitmapDescriptorFactory.defaultMarker(color))
				.visible(getVisible(pin)));

		geoposts.put(m, pin);
	}

	/**
	 * Initialize the map if it is not already, ensures map is avaliable to
	 * activity
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}
	}

	/************ Options Menu ***************/

	/**
	 * Creates the options menu on start up of the activity. Currently, always
	 * returns true.
	 * 
	 * @param menu
	 *            , menu to place items in
	 * @return true to have menu displayed
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("DEBUG", "onCreateOptionsMenu");
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// Create a drop down menu in the menu bar
		Spinner spinner = (Spinner) menu.findItem(R.id.sort_options)
				.getActionView();
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.sorting_array, R.layout.spinner);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		// set listener on spinner
		spinner.setOnItemSelectedListener(this);
		return true;
	}

	/**
	 * Handle menu item selections
	 * 
	 * @param item the clicked menu item
	 * @return true if event was handled, false to have default
	 *         onOptionsItemSelected happen.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_help:
			openHelpFragment();
			return true;
		case R.id.action_profile:
			openProfileActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Called when the user selects an item from the sorting spinner
	 * @param parent The AdapterView where the selection happened
	 * @param view The view within the AdapterView that was clicked
	 * @param pos The position of the view in the adapter
	 * @param id The row id of the item that is selected
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		String option = (String) parent.getItemAtPosition(pos);
		if (option.equals("All Posts")) {
			sortingOption = SortingOption.ALL;
		} else if (option.equals("Viewed")) {
			sortingOption = SortingOption.VIEWED;
		} else if (option.equals("Locked")) {
			sortingOption = SortingOption.LOCKED;
		} else if (option.equals("My Posts")) {
			sortingOption = SortingOption.MY_POSTS;
		} else if (option.equals("Friends")) {
			sortingOption = SortingOption.FB_FRIENDS;
		}

		// refresh the current pins
		refreshLocalPins();
	}

	/**
	 * Loop through all pins stored locally and set the correct ones to be
	 * visible
	 */
	private void refreshLocalPins() {
		for (Marker m : geoposts.keySet()) {
			Pin pin = geoposts.get(m);
			m.setVisible(getVisible(pin));
		}
	}
	
	/**
	 * Return whether the given pin should be displayed given
	 * the current filter option. 
	 * 
	 * @param p the pin to be displayed or not
	 * @return true if the pin should be visible, false otherwise
	 */
	private boolean getVisible(Pin p) {
		switch (sortingOption) {
		case LOCKED:
			return p.isLocked();
		
		case VIEWED:
			return !p.isLocked() && 
					!wasPostedByUser(p);
			
		case MY_POSTS:
			return wasPostedByUser(p);
			
		case FB_FRIENDS:
			return friends.contains(p.getFacebookID());
			
		default:
			return true;
		}
	}
	
	/**
	 * Return whether the given pin was posted by the current user
	 * 
	 * @param p the pin to check
	 * @return true if the current user posted p, false otherwise
	 */
	private boolean wasPostedByUser(Pin p) {
		return p.getFacebookID() != null
				&& p.getFacebookID().equals(currentUser.getFacebookID());
	}

	/**
	 * Called from the sorting spinner when nothing is selected
	 */
	public void onNothingSelected(AdapterView<?> parent) {
		// Required for interface, but not needed
	}

	/**
	 * Open the help fragment
	 */
	private void openHelpFragment() {
		DialogFragment newFragment = new HelpFragment();
		newFragment.show(getSupportFragmentManager(), "help");
	}

	/**
	 * Open the profile activity Pass it the user's name, number of posts, and
	 * number of viewed posts
	 */
	private void openProfileActivity() {
		Intent intent = new Intent(this, ProfileActivity.class);

		// get the user again to update posts and views
		currentUser = dbq.getCurrentUser();

		assert (currentUser != null);
		intent.putExtra("edu.washington.geopost.USERNAME",
				currentUser.getName());
		intent.putExtra("edu.washington.geopost.FACEBOOKID",
				currentUser.getFacebookID());
		intent.putExtra("edu.washington.geopost.NUM_POSTED",
				currentUser.getNumPosted());
		intent.putExtra("edu.washington.geopost.NUM_VIEWED",
				currentUser.getNumViewed());
		startActivity(intent);
	}

	/************ View pin logic ***************/

	/**
	 * On clicking a marker, show the marker window if there is not already one
	 * shown. Otherwise, hide the marker window.
	 * 
	 * @param marker the clicked marker (or pin)
	 * @return true if event was handled, returning false causes default
	 *         onMarkerClick to run, which will incorrectly display the window
	 *         here.
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.d("onMarkerClick", "marker clicked");
		assert (marker != null);
		Pin pin = geoposts.get(marker);
		if (pin == null) {
			Log.d("onMarkerClick", "clicked on marker not found in map");
			return true;
		}

		if (markerWindowShown) { // window is showing, hide it
			hidePinWindow(marker);
		} else { // window not showing, see if we should show it
			if (isInRange(marker) && pin.isLocked()) {
				Log.d("onMarkerClick", "attempting to unlock in-range pin");
				if (!isNetworkAvailable()) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Network unavailable", Toast.LENGTH_LONG);
					toast.show();
					return true;
				}
				Pin p = dbs.unlockPin(pin);
				if (p != null) { // unlocked new pin
					geoposts.put(marker, p);
					showPinWindow(p, marker);
				} else { // unlocking failed
					hidePinWindow(marker);
					Log.d("onMarkerClick", "Failed to unlock pin");
				}
			} else if (!pin.isLocked()) { // pin already unlocked
				showPinWindow(pin, marker);
				Log.d("onMarkerClick", "viewed previously unlocked pin");
			} else { // pin is locked
				Log.d("onMarkerClick", "clicked on locked/out of range pin");
				hidePinWindow(marker);
				Toast toast = Toast.makeText(getApplicationContext(), "Locked",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		return true;
	}
	
	/**
	 * Brings up the pin window associated with the marker to show
	 * the pin's message and photo
	 * 
	 * @param pin The pin associated with given marker that contains
	 * 				message, photo, and name
	 * @param marker The marker the user clicked on
	 */
	private void showPinWindow(Pin pin, Marker marker) {
		Bitmap photo = pin.getPhoto();
		if (photo != null) {
			vpw.setPhoto(photo);
		}
		marker.showInfoWindow();
		markerWindowShown = true;
	}
	
	/**
	 * Closes the pin window associated with the marker
	 * 
	 * @param marker The marker the user clicked on
	 */
	private void hidePinWindow(Marker marker) {
		vpw.closePhoto();
		marker.hideInfoWindow();
		markerWindowShown = false;
	}

	/**
	 * Returns whether the marker is in range of the user's GPS position. The
	 * user must be RANGE_RADIUS coordinates or less away from the marker to be
	 * in range. If the user's location cannot be found, displays a message
	 * saying so.
	 * 
	 * @param marker the marker to verify
	 * @return true if the marker is in range, false otherwise
	 */
	private boolean isInRange(Marker marker) {
		Location loc = locationClient.getLastLocation();
		if (loc == null) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Could not find your location", Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		// Get the user's lat/lng coordinates
		double userLat = loc.getLatitude();
		double userLng = loc.getLongitude();

		// Get the pin's lat/lng coordinates
		Pin p = geoposts.get(marker);
		double pinLat = p.getLocation().latitude;
		double pinLng = p.getLocation().longitude;

		// Return if the distance between points is within unlocked radius
		double distance = distance(userLat, userLng, pinLat, pinLng);
		return distance <= coordToMeters(RANGE_RADIUS);
	}

	/**
	 * Gets distance between two latlng points
	 * 
	 * @param startLat The latitude of the initial point
	 * @param startLng The longitude of the initial point
	 * @param endLat The latitude of the end point
	 * @param endLng The latitude of the end point
	 * @return Uses the haversine formula to calculate and return the distance
	 *         between two lat/lng points on the earth in meters
	 */
	private double distance(double startLat, double startLng, double endLat,
			double endLng) {
		double dLat = Math.toRadians(endLat - startLat);
		double dLon = Math.toRadians(endLng - startLng);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(startLat))
				* Math.cos(Math.toRadians(endLat)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);

		double c = 2 * Math.asin(Math.sqrt(a));
		return EARTH_RADIUS * c;
	}

	/**************** Post pin logic ****************/

	/**
	 * Method called when the post button is clicked Creates and displays a new
	 * PostFragment, passing it the current latitude and longitude of the user's
	 * location. It the current location cannot be found, displays a message
	 * saying so.
	 * 
	 * @param view
	 *            the clicked post button
	 */
	public void onPostButtonClick(View view) {
		Log.d("LC", "Post Clicked");
		Location loc = locationClient.getLastLocation();
		if (loc == null) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Unable to find your location", Toast.LENGTH_SHORT);
			toast.show();
		} else if (!isNetworkAvailable()) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Network unavailable", Toast.LENGTH_SHORT);
			toast.show();
		} else {
			DialogFragment newFragment = new PostFragment();
			newFragment.show(getSupportFragmentManager(), "post");
		}
	}

	@Override
	/**
	 * This has to be overridden so PostFragment's camera intent is
	 * sent to the correct activity
	 * @param requestCode The integer request code originally supplied to 
	 * 						startActivityForResult(), allowing you to identify 
	 * 						who this result came from.
	 * @param resultCode The integer result code returned by the 
	 * 						child activity through its setResult()
	 * @param data An Intent, which can return result data to the caller 
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("DEBUG", "onActivityResult");
		Log.d("CAM", "Inside main onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * The dialog fragment receives a reference to this Activity through the
	 * Fragment.onAttach() callback, which it uses to call the following methods
	 * defined by the PostFragment.PostDialogListener interface This method is
	 * called on a click of the "Post" button from a PostFragment Adds a pin to
	 * the map at the coordinates given with the given message
	 * 
	 * @param dialog
	 *            a reference to the fragment this is listening on
	 * @param coord
	 *            the coordinates to create a pin at
	 * @param message
	 *            the message for the new pin
	 */
	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String message,
			Bitmap photo) {
		Log.d("PHOTO", "onDialogPositiveClick start");
		Location loc = locationClient.getLastLocation();
		// check for no location
		if (loc == null) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Unable to post: cannot find your location",
					Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		if (!isNetworkAvailable()) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Unable to post: Network unavailable", Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		// check for empty message
		if (message.length() == 0) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Sorry, cannot post an empty message", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		LatLng coord = new LatLng(loc.getLatitude(), loc.getLongitude());

		Pin pin = dbs.postPin(coord, message, photo);
		if (pin == null) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Posting failed. Please try again.",
					Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		addPin(pin);
	}

	/**************** location listener ****************/
	/**
	 * Redraws the user's unlocking radius to center around the new location
	 * 
	 * @param Location
	 *            The new location the user has moved to
	 */
	@Override
	public void onLocationChanged(Location location) {
		// Remove the old radius
		if (unlockedRadius != null) {
			unlockedRadius.remove();
		}
		// Draw the new radius
		if (location != null) {
			drawCircle(new LatLng(location.getLatitude(),
					location.getLongitude()));
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Unable to find your location", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * Draw the unlock range circle on the map
	 * 
	 * @param center The coordinate center where the user is located on the map
	 *            	which serves as the epicenter of the circle to draw
	 */
	public void drawCircle(LatLng center) {
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(center);
		circleOptions.radius(coordToMeters(RANGE_RADIUS));
		circleOptions.strokeColor(Color.RED);
		circleOptions.strokeWidth(BORDER_THICKNESS);
		// Add the circle to the map
		unlockedRadius = map.addCircle(circleOptions);
	}

	/**
	 * Changes the given difference in coordinates to a difference
	 * in meters
	 * 
	 * @param difference The lat/lng difference in distance between two points
	 * @return The same distance in meters
	 */
	private double coordToMeters(double difference) {
		return difference * COORD_IN_METERS;
	}

	/**
	 * Check if we have network
	 * 
	 * @return True if the phone is connected to any network, false otherwise
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**************** Map refresh logic ****************/

	/**
	 * Activated when camera is changed, panning or zooming. This method will
	 * trigger a call to updateMap() to redraw the relevant pins
	 * 
	 * @param CameraPosition The position of the user's camera
	 */
	@Override
	public void onCameraChange(CameraPosition cp) {
		Log.d("Event", "onCameraChange fired");
		refreshThread.cancel(true); // If another query to onCameraChange is
									// still
									// running, stop this so that this new
									// change is seen
		VisibleRegion vr = map.getProjection().getVisibleRegion();
		if (vr != null) {
			LatLng sw = vr.latLngBounds.southwest;
			LatLng ne = vr.latLngBounds.northeast;
			Log.d("updateMap", " sw,lat " + sw.latitude + " sw,lng "
					+ sw.longitude + " ne,lat " + ne.latitude + " ne,lng "
					+ ne.longitude);

			// Create background task that will query the database
			// and upon return, draw the updated pin/markers on the map
			refreshThread = new RefreshMapTask();
			loadingWheel.setVisibility(View.VISIBLE);
			refreshThread.execute(sw, ne);
			
		}
	}

	/**
	 * 
	 * @author Matt Asynchronous task used to refresh the pins on the map. The
	 *         querying to the database is done in the background and draws the
	 *         results once it gets resulting pins Extends from AsyncTask which
	 *         is an asynchronous task handler
	 */
	private class RefreshMapTask extends AsyncTask<Object, Void, Set<Pin>> {

		/**
		 * Queries the database for pins in view as background task
		 * 
		 * @param LatLng sw The southwest corner of the user's view
		 * @param LatLng ne The northeast corner of the user's view
		 * @return Set<Pin> The resulting pins from the database that are within
		 *         the bounding box from the two points
		 */
		@Override
		protected Set<Pin> doInBackground(Object... params) {
			Log.d("Background!", "Background start!");
			assert (params.length >= 2);

			LatLng sw = (LatLng) params[0];
			LatLng ne = (LatLng) params[1];

			Set<Pin> p = dbq.getPins(sw, ne);
			if (p == null) {
				Log.d("doInBackground", "null query");
			}
			return p;
		}

		/**
		 * This is called after doInBackground returns, with its return value
		 * Draws the pins onto the map
		 * 
		 * @param Set <Pin> The pins from the background task that need to be
		 *            drawn onto the map
		 */
		protected void onPostExecute(Set<Pin> pins) {
			Log.d("onPostExecute", "executing, pin drawing");
			drawMarkers(pins);
			Log.d("LoadWheel", "onPostExecute setVisible before");
			loadingWheel.setVisibility(View.GONE);
			Log.d("LoadWheel", "onPostExecute setVisible before");
		}
	}

	/**
	 * Takes a set of Pin objects and ensures that they are displayed on the
	 * map, removes any pins that are currently displayed if they are not also
	 * in the supplied set.
	 * 
	 * @param pins
	 *            , set of pins to draw onto the map, passing null causes map to
	 *            be cleared
	 */
	public void drawMarkers(Set<Pin> pins) {
		assert (geoposts != null);
		if (pins == null) {
			geoposts.clear();
			map.clear();
			Toast toast = Toast.makeText(getApplicationContext(),
					"Unable to load posts", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		/*
		 * First remove old pins that aren't in view now
		 */
		HashSet<Marker> currentMarkers = new HashSet<Marker>(geoposts.keySet());
		for (Marker m : currentMarkers) {
			Pin p = geoposts.get(m);
			if (!pins.contains(p)) {
				// m is no longer in our scope
				m.remove();
				geoposts.remove(m);
			}
		}

		/*
		 * Now add new pins that weren't drawn before
		 */
		HashSet<Pin> currentPins = new HashSet<Pin>(geoposts.values());
		for (Pin p : pins) {
			if (!currentPins.contains(p)) {
				// this will add p to geoposts
				Log.d("drawMarkers", "added pin to map");
				addPin(p);
			}
		}
		Log.d("drawMarkers", "drew markers");
	}

	/* For test */
	
	/**
	 * @return the value of includeViewed
	 */
	public boolean isIncludeViewed() {
		return sortingOption == SortingOption.ALL ||
				sortingOption == SortingOption.VIEWED ||
				sortingOption == SortingOption.FB_FRIENDS;
	}

	/**
	 * @return the value of includeLocked
	 */
	public boolean isIncludeLocked() {
		return sortingOption == SortingOption.ALL ||
				sortingOption == SortingOption.LOCKED ||
				sortingOption == SortingOption.FB_FRIENDS;
	}

	/**
	 * @return the value of includePosted
	 */
	public boolean isIncludePosted() {
		return sortingOption == SortingOption.ALL ||
				sortingOption == SortingOption.MY_POSTS;
	}

	/**
	 * @return the value of includeFriends
	 */
	public boolean isIncludeFriends() {
		return sortingOption == SortingOption.FB_FRIENDS;
	}
}
