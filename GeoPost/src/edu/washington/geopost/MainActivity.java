package edu.washington.geopost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 
						  implements OnMarkerClickListener, 
									 LocationListener, 
									 PostFragment.PostDialogListener,
									 OnCameraChangeListener {
	
	public static final float INIT_ZOOM = 15;
	public static final String TAG = "GeoPost";
	public static final float RADIUS_WIDTH = 4;
	public static final double RANGE_RADIUS = 0.004;
	
	private LocationManager locationManager;
	private String provider;
	private GoogleMap map;
	private boolean markerWindowShown;
	private RefreshMapTask refreshThread;
	private Circle unlockedRadius;
	
	private DBQuery dbq;
	private DBStore dbs;
	
	// Location a pin is currently being posted to
	private Location postLocation;
	
	// A map of all pins currently drawn in the app
	private HashMap<Marker, Pin> geoposts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMapIfNeeded();

		// Setup collection
		geoposts = new HashMap<Marker, Pin>();
		
		dbq = new DBQuery();
		dbs = new DBStore();

		// Set the pin pop up windows to use the ViewPinWindow class
		map.setInfoWindowAdapter(new ViewPinWindow(this));

		markerWindowShown = false;
		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);

		// Initialize provider (this provider doesn't always work)
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);

		map.setMyLocationEnabled(true);
		map.setOnMarkerClickListener(this);
		map.setOnCameraChangeListener(this);
		map.getUiSettings().setRotateGesturesEnabled(false);
		
		
		// Make the app open up to your current location 
		Location currentLocation = getLastKnownLocation();
		if (currentLocation != null) {
			LatLng myLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, INIT_ZOOM));
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Unable to find your location", 
					Toast.LENGTH_SHORT);
			toast.show();
		}
		
		// Draw the unlocking radius
		drawCircle(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
		
		// Create the Async Task that will be used to refresh
		// pins on the screen
		refreshThread = new RefreshMapTask();
		
	}
	
    // Loops through available providers and finds one that returns a location which
    // is not null with the best accuracy
    // Uses this provider to get the current location
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
    }
    
	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * Add a pin to the map
     * @param pin the pin to be added
     */
    private void addPin(Pin pin){
    	// TODO use pin.getUser() instead of "anonymous"
    	Marker m = map.addMarker(new MarkerOptions()
    	.title(pin.getMessage())
    	.snippet("anonymous")
    	.position(pin.getCoord()));
    	
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
				if (dbs.unlockPin(pin)) {  // unlocked new pin
					// TODO: pin now has to be updated in the geoposts map
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
	
	// Draws the circle on the map that shows the user's radius
	// for viewing/unlocking pins
	public void drawCircle(LatLng center) {
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(center);
		circleOptions.radius(coordToMeters(RANGE_RADIUS));
		circleOptions.strokeColor(Color.RED);
		circleOptions.strokeWidth(RADIUS_WIDTH);
		// Add the circle to the map
	    unlockedRadius = map.addCircle(circleOptions);
	}
	
	// Given a difference between two coords (lat/lng),
	// returns the distance in meters
	private double coordToMeters(double difference) {
		return difference * 111319.9;
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
		Location l = getLastKnownLocation();
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
		Location l = getLastKnownLocation();
		if (l == null) {
			Toast toast = Toast.makeText(getApplicationContext(), "Unable to find your location", 
										Toast.LENGTH_SHORT);
			toast.show();
		} else {	
			DialogFragment newFragment = new PostFragment();
			postLocation = l;
			
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
    	Pin pin = dbs.postPin(coord, message);
        addPin(pin);
    }
    
    /**************** location listener ****************/
	@Override
	public void onLocationChanged(Location location) {
		// Remove the old radius
		unlockedRadius.remove();
		// Draw the new radius
		drawCircle(new LatLng(location.getLatitude(), location.getLongitude()));
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
		
			refreshThread = new RefreshMapTask();
			refreshThread.execute(sw, ne);
		}
		//updateMap();
	}
	
	// Asynchronous task used to refresh the pins on the map.
	// The querying to the database is done in the background
	// and draws the results once it gets resulting pins
	private class RefreshMapTask extends AsyncTask<Object, Void, Set<Pin>> {

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
	
	/**
	 * Query database and redraw pins that are now in view.
	 * This function is used in onCameraChanged() to redraw pins for new camera bounds
	 */
	public synchronized void updateMap(){
		// query DB based on map boundries
		VisibleRegion vr = map.getProjection().getVisibleRegion();

		if (vr != null){
			LatLng sw = vr.latLngBounds.southwest;
			LatLng ne = vr.latLngBounds.northeast;
			Log.d("updateMap", " sw,lat " + sw.latitude + " sw,lng " + sw.longitude + " ne,lat " + ne.latitude + " ne,lng " + ne.longitude);
			
			Set<Pin> pins = dbq.getPins(sw, ne);
			
			Log.d("updateMap", "got pins " + pins.size());
			
			// draw these pins
			drawMarkers(pins);
		} else {
			assert(false);
		}
		/*
		Set<Pin> pins = new HashSet<Pin>();
		pins.add(new Pin(new LatLng(0, 0), "abc", "Hello1"));
		pins.add(new Pin(new LatLng(4, 4), "def", "Hello2"));
		pins.add(new Pin(new LatLng(8, 8), "jkl", "Hello3"));
		*/	
	}
}
