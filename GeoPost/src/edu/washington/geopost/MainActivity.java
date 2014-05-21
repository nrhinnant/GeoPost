package edu.washington.geopost;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/** 
 * @author Matt, Mike, Ethan
 *
 */
public class MainActivity extends FragmentActivity 
						  implements OnMarkerClickListener, 
									 LocationListener, 
									 PostFragment.PostDialogListener,
									 OnCameraChangeListener,
									 ConnectionCallbacks, OnConnectionFailedListener,
									 com.google.android.gms.location.LocationListener {
	
	// Zoom level upon opening app
	public static final float INIT_ZOOM = 15;
	// TODO: Put this in the strings.xml
	public static final String TAG = "GeoPost";
	// Thickness of the unlocking circle border line
	public static final float BORDER_THICKNESS = 4;
	// Scale of the unlocking circle in lat/long coord difference
	public static final double RANGE_RADIUS = 0.004;
	// The meters between two lat/lng lines in meters
	public static final double COORD_IN_METERS = 111319.9;
	
	// Location related fields 
	//private LocationManager locationManager;
	private String provider;
	// The main map that is shown to the user
	private GoogleMap map;
	// Check to see if marker window is open
	private boolean markerWindowShown;
	// The background thread that handles getting pins from database
	private RefreshMapTask refreshThread;
	// The circle drawn on the map
	private Circle unlockedRadius;
	
	// Database interfaces
	private DBQuery dbq;
	private DBStore dbs;
	
	private User currentUser;
	
	// A map of all pins currently drawn in the app
	private HashMap<Marker, Pin> geoposts;
	
	private LocationClient locationClient;
	private LocationRequest locationRequest;

	/**
	 * @param Bundle The saved instance state of the app
	 * Called upon opening of the activity. Initializes all of the
	 * UI components, location, database interfaces, and makes
	 * initial call to zoom in on location and get pins to put on map.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();
		
		final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (result != ConnectionResult.SUCCESS) {
			Toast toast = Toast.makeText(this, "Google Play service is not available: " + result, Toast.LENGTH_LONG);
			toast.show();
		}
		locationClient = new LocationClient(this, this, this);
		Log.d("LC", "Created location client");
		locationClient.connect();
		Log.d("LC", "Connected location client");
		
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        locationRequest.setInterval(5 * 1000);
        // Set the fastest update interval to 1 second
        locationRequest.setFastestInterval(1 * 1000);

		// Setup collection of markers on map to actual pins
		geoposts = new HashMap<Marker, Pin>();
		
		// Initialize the database interfaces
		dbq = new DBQuery();
		dbs = new DBStore();
		
		currentUser = dbq.getCurrentUser();

		// Set the pin pop up windows to use the ViewPinWindow class
		map.setInfoWindowAdapter(new ViewPinWindow(this));

		markerWindowShown = false;

		map.setMyLocationEnabled(true);
		map.setOnMarkerClickListener(this);
		map.setOnCameraChangeListener(this);
		map.getUiSettings().setRotateGesturesEnabled(false);
		
		// Draw the unlocking radius
		//drawCircle(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
		
		// Create the Async Task that will be used to refresh
		// pins on the screen
		refreshThread = new RefreshMapTask();
		
	}
	
	 @Override 
    protected void onStop() { 
    	Log.d("OnStop","Stop Periodic Updates");
        // If the client is connected 
        if (locationClient.isConnected()) {
            stopPeriodicUpdates(); 
        } 
        /* 
         * After disconnect() is called, the client is 
         * considered "dead". 
         */ 
        locationClient.disconnect();
        super.onStop(); 
    } 
	 
	//helper functions 
    private void stopPeriodicUpdates() {
    	locationClient.removeLocationUpdates(this);
    } 
     
    private void startPeriodicUpdates() { 
    	locationClient.requestLocationUpdates(locationRequest, this);
    } 
	
	/**
	 * Disable the back button
	 */
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	
	/*
	/**
	 * Loops through available providers and finds one that returns a location which
	 * is not null with the best accuracy
	 * @return The most accurate location available or null, if no location
	 * service is available
	 *
    private Location getLastKnownLocation() {
    	List<String> providers = locationManager.getProviders(true);
    	Location bestLocation = null;
    	for (String provider : providers) {
    		Location l = locationManager.getLastKnownLocation(provider);

    		if (l == null) {
    			continue;
    		}
    		if (bestLocation == null
    				|| l.getAccuracy() < bestLocation.getAccuracy()) {
    			bestLocation = l;
    			this.provider = provider;
    		}
    	}
    	return bestLocation;
    } */
    
	/**
	 * Request updates at startup 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//locationManager.requestLocationUpdates(provider, 400, 1, this);
		locationClient.connect();
	}

	/**
	 *  Remove the locationlistener updates when Activity is paused 
	*/
	@Override
	protected void onPause() {
		super.onPause();
		//locationManager.removeUpdates(this);
		locationClient.disconnect();
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
		// Make the app open up to your current location 
		Location currentLocation = locationClient.getLastLocation();
		if (currentLocation != null) {
			LatLng myLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, INIT_ZOOM));
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Unable to find your location", 
					Toast.LENGTH_SHORT);
			toast.show();
		}
		
		drawCircle(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

		Location loc = locationClient.getLastLocation();
		Log.d("XXX", "location=" + loc.toString());

		startPeriodicUpdates();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
	}
	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
	}
    
    /**
     * Add a pin to the map
     * @param pin the pin to be added
     */
    private void addPin(Pin pin){
    	float color = BitmapDescriptorFactory.HUE_RED;
    	Log.d("addPin", pin.getUser());
    	if (pin.getUser().equals(currentUser.getName())) {
    		color = BitmapDescriptorFactory.HUE_VIOLET;
    	} else if (!pin.isLocked()) {
    		color = (float) 220.0;
    	}
    	
    	// TODO use pin.getUser() instead of "anonymous"
    	Marker m = map.addMarker(new MarkerOptions()
    	.title(pin.getMessage())
    	.snippet(pin.getUser())
    	.position(pin.getLocation())
    	.icon(BitmapDescriptorFactory.defaultMarker(color)));
    	
    	geoposts.put(m, pin);
    }
     
    /**
     * Initialize the map if possible
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                // The Map is verified. It is now safe to manipulate the map.
            }
        }
    }
    
	/************ Options Menu ***************/
	
	/**
	 * Creates the options menu on start up of the activity.
	 * Currently, always returns true.
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Handle menu item selections
     * 
     * @param item the clicked menu item
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
            case R.id.action_logout:
            	logout();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Open the help fragment
     */
    private void openHelpFragment() {
    	DialogFragment newFragment = new HelpFragment();
    	newFragment.show(getSupportFragmentManager(), "help");
    }
    
    /**
     * Open the profile activity
     * Pass it the user's name, number of posts, and number of viewed posts
     */
    private void openProfileActivity() {
    	Intent intent = new Intent(this, ProfileActivity.class);
    	
    	// get the user again to update posts and views
    	currentUser = dbq.getCurrentUser();
    	
    	User u = currentUser;
    	assert(u != null);
    	intent.putExtra("edu.washington.geopost.USERNAME", u.getName());
    	intent.putExtra("edu.washington.geopost.FACEBOOKID", u.getFacebookID());
    	intent.putExtra("edu.washington.geopost.NUM_POSTED", u.getNumPosted());
    	intent.putExtra("edu.washington.geopost.NUM_VIEWED", u.getNumViewed());
    	startActivity(intent);
    }
    
    /**
     * Log out the user
     */
    private void logout() {
    	//TODO: this
    }
    
    /************ View pin logic ***************/

    /**
     * On clicking a marker, show the marker window if there is not already one shown. 
     * Otherwise, hide the marker window. 
     * 
     * @param marker the clicked marker (or pin)
     * @return true if event was handled, returning false causes default behavior to run
     */
	@Override
	public boolean onMarkerClick(Marker marker) {
		Log.d("onMarkerClick", "marker clicked");
		assert(marker != null);
		Pin pin = geoposts.get(marker);
		if (pin == null){
			Log.d("onMarkerClick", "clicked on marker not found in map");
			return true;
		}
		
		if (markerWindowShown) { // window is showing, hide it
			marker.hideInfoWindow();
			markerWindowShown = false;
		} else {  // window not showing, see if we should show it
			if (isInRange(marker) && pin.isLocked()) {
				Log.d("onMarkerClick", "attempting to unlock in-range pin");
				Pin p = dbs.unlockPin(pin);
				if (p != null) {  // unlocked new pin
					// TODO: pin now has to be updated in the geoposts map
					geoposts.put(marker, p);
					marker.showInfoWindow();
					markerWindowShown = true;
				} else {  // unlocking failed
					marker.hideInfoWindow();
					markerWindowShown = false;
					Log.d("onMarkerClick", "Failed to unlock pin");
				}
			} else if (!pin.isLocked()) {  // pin already unlocked
				marker.showInfoWindow();
				markerWindowShown = true;
				Log.d("onMarkerClick", "viewed previously unlocked pin");
			} else {  // pin is locked
				Log.d("onMarkerClick", "clicked on locked/out of range pin");
				marker.hideInfoWindow();
				markerWindowShown = false;
				Toast toast = Toast.makeText(getApplicationContext(), "Locked", 
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		return true;
	}
	
	/**
	 * Returns whether the marker is in range of the user's GPS position. 
	 * The user must be RANGE_RADIUS coordinates or less away from the marker
	 * to be in range. 
	 * If the user's location cannot be found, displays a message saying so. 
	 * @param marker the marker to verify
	 * @return true if the marker is in range, false otherwise
	 */
	private boolean isInRange(Marker marker) {
		//Location l = getLastKnownLocation();
		Location l = locationClient.getLastLocation();
		if (l == null) {
			Toast toast = Toast.makeText(getApplicationContext(), "Could not find your location", 
										Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		double userLat = l.getLatitude();
		double userLng = l.getLongitude();
		Pin p = geoposts.get(marker);
		double pinLat = p.getLocation().latitude;
		double pinLng = p.getLocation().longitude;
		double res = Math.sqrt(Math.pow(userLat - pinLat, 2) + Math.pow(userLng - pinLng, 2));
		return res <= RANGE_RADIUS;
	}
	
	/**************** Post pin logic ****************/
	
	/**
	 * Method called when the post button is clicked
	 * Creates and displays a new PostFragment,
	 * passing it the current latitude and longitude of the
	 * user's location. 
	 * It the current location cannot be found, displays a 
	 * message saying so. 
	 * @param view the clicked post button
	 */
	public void onPostButtonClick(View view) {
		//Location l = getLastKnownLocation();
		Location l = locationClient.getLastLocation();
		if (l == null) {
			Toast toast = Toast.makeText(getApplicationContext(), "Unable to find your location", 
										Toast.LENGTH_SHORT);
			toast.show();
		} else {	
			DialogFragment newFragment = new PostFragment();
			
			// Pass the current coordinates to the PostFragment
			Bundle args = new Bundle();
			double lat = l.getLatitude();
			double lng = l.getLongitude();
		    args.putDouble("lat", lat);
		    args.putDouble("lng", lng);
		    newFragment.setArguments(args);
		    
		    newFragment.show(getSupportFragmentManager(), "post");
		}
	}
	
	/**
	 * The dialog fragment receives a reference to this Activity through the
     * Fragment.onAttach() callback, which it uses to call the following methods
     * defined by the PostFragment.PostDialogListener interface
	 * This method is called on a click of the "Post" button from a PostFragment
	 * Adds a pin to the map at the coordinates given with the given message
	 * 
	 * @param dialog a reference to the fragment this is listening on
	 * @param coord the coordinates to create a pin at
	 * @param message the message for the new pin
	 */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, LatLng coord, String message) {
    	
    	// check for empty message
    	if (message.length() == 0) {
    		Toast toast = Toast.makeText(getApplicationContext(), "Sorry, cannot post an empty message", 
					Toast.LENGTH_SHORT);
    		toast.show();
    		return;
    	}
    	
    	Pin pin = dbs.postPin(coord, message);
        addPin(pin);
    }
    
    /**************** location listener ****************/
    /**
     * @param Location The new location the user has moved to
     * Redraws the user's unlocking radius to center around the new location
     */
	@Override
	public void onLocationChanged(Location location) {
		// Remove the old radius
		unlockedRadius.remove();
		// Draw the new radius
		drawCircle(new LatLng(location.getLatitude(), location.getLongitude()));
	}
	
	/**
	 * 
	 * @param center The coordinate center where the user is located on the map
	 * 				which serves as the epicenter of the circle to draw
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
	 * 
	 * @param difference The lat/lng difference in distance between two points
	 * @return The same distance in meters
	 */
	private double coordToMeters(double difference) {
		return difference * COORD_IN_METERS;
	}

	// Inherited by LocationListener 
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	// Inherited by LocationListener 
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	// Inherited by LocationListener 
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	/**************** Map refresh logic ****************/
	
	/**
	 * Activated when camera is changed, panning or zooming.  This method will trigger a call to
	 * updateMap() to redraw the relevant pins
	 * @param CameraPosition The position of the user's camera
	 */
	@Override
	public void onCameraChange(CameraPosition cp) {
		Log.d("Event", "onCameraChange fired");
		refreshThread.cancel(true); // If another query to onCameraChange is still
									// running, stop this so that this new change is seen
		VisibleRegion vr = map.getProjection().getVisibleRegion();
		if (vr != null){
			LatLng sw = vr.latLngBounds.southwest;
			LatLng ne = vr.latLngBounds.northeast;
			Log.d("updateMap", " sw,lat " + sw.latitude + " sw,lng " + sw.longitude + " ne,lat " + ne.latitude + " ne,lng " + ne.longitude);
		
			// Create background task that will query the database
			// and upon return, draw the updated pin/markers on the map
			refreshThread = new RefreshMapTask();
			refreshThread.execute(sw, ne);
		}
	}
	
	// Asynchronous task used to refresh the pins on the map.
	// The querying to the database is done in the background
	// and draws the results once it gets resulting pins
	/**
	 * 
	 * @author Matt 
	 *  Asynchronous task used to refresh the pins on the map.
	 *  The querying to the database is done in the background
	 *  and draws the results once it gets resulting pins
	 *	Extends from AsyncTask which is an asynchronous task handler
	 */
	private class RefreshMapTask extends AsyncTask<Object, Void, Set<Pin>> {

		/**
		 * @param LatLng sw The southwest corner of the user's view
		 * @param LatLng ne The northeast corner of the user's view
		 * @return Set<Pin> The resulting pins from the database that
		 * 					are within the bounding box from the two points
		 */
		@Override
		protected Set<Pin> doInBackground(Object... params) {
			Log.d("Background!", "Background start!");
			assert(params.length >= 2);
			
			LatLng sw = (LatLng) params[0];
			LatLng ne = (LatLng) params[1];
				
			Set<Pin> p = dbq.getPins(sw, ne);
			if (p == null){
				Log.d("doInBackground", "null query");
			}
			return p;
		}
		
		/**
		 * @param Set<Pin> The pins from the background task that
		 * 					need to be drawn onto the map
		 * Draws the pins onto the map
		 */
		protected void onPostExecute(Set<Pin> pins) {
			Log.d("Background!", "executing");
			drawMarkers(pins);
		}	
	}
	
	/**
	 * Takes a set of Pin objects and ensures that they are displayed on the map,
	 * removes any pins that are currently displayed if they are not also in the 
	 * supplied set.
	 * @param pins, set of pins to draw onto the map
	 * if pins is null, the map should be cleared
	 */
	public void drawMarkers(Set<Pin> pins){
		assert(geoposts != null);
		if (pins == null){
			geoposts.clear();
			map.clear();
			Toast toast = Toast.makeText(getApplicationContext(), "Unable to load posts", 
					Toast.LENGTH_SHORT);
			toast.show();
			return;
		}

		/*
		 * First remove old pins that aren't in view now
		 */
		HashSet<Marker> temp = new HashSet<Marker>();
		for(Marker m : geoposts.keySet()){
			Pin p = geoposts.get(m);
			if (!pins.contains(p)){
				// m is no longer in our scope
				temp.add(m);
			}
		}
		
		// Now remove the markers from the map and geoposts
		for (Marker m : temp) {
			m.remove();
			geoposts.remove(m);
		}
		
		/*
		 * Now add new pins that weren't drawn before
		 */
		HashSet<Pin> pinvalues = new HashSet<Pin>(geoposts.values());
		for (Pin p : pins){
			if (!pinvalues.contains(p)){
				// this will add p to geoposts
				Log.d("drawMarkers", "added pin to map");
				addPin(p);
			}
		}
		Log.d("drawMarkers", "drew markers");
	}
	
}
