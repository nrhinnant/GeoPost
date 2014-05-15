package edu.washington.geopost;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

public class MainActivity extends FragmentActivity 
						  implements OnMarkerClickListener, 
									 LocationListener, 
									 PostFragment.PostDialogListener,
									 OnCameraChangeListener {
	
	public static final float INIT_ZOOM = 15;
	
	static final String TAG = "GeoPost";
	private final double RANGE_RADIUS = 0.01;
	private LocationManager locationManager;
	private String provider;
	private GoogleMap map;
	private boolean markerWindowShown;
	
	private DBQuery dbq;
	private DBStore dbs;
	
	// Location a pin is currently being posted to
	private Location postLocation;
	
	/*
	 * A map of all pins currently drawn in the app
	 */
	private HashMap<Marker, Pin> geoposts;
	private final String appID = ""; 		// change this to your Parse application id
	private final String clientKey = ""; 	// change this to your Parse client key

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
		//Parse.initialize(this, appID, clientKey);
		
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
		
		// Populate the map window with pins
		//updateMap();
		addPin(new Pin(true, new LatLng(47.5, -122.4), "mike", "aklsjdflkajsd", "helloooo from mike"));
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
     * @param pin
     */
    private void addPin(Pin pin){
    	User currentUser = dbq.getCurrentUser();
    	String name = null;
    	if (currentUser != null) {
    		name = currentUser.getName();
    	}
    	
    	//TODO real values
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
		Pin pin = geoposts.get(marker);
		if (pin == null){
			Log.d("onMarkerClick", "clicked on marker not found in map");
			return true;
		}
		
		// Note: marker.isInfoWindowShown() has a bug, don't use it
		
		if (markerWindowShown) { // window is showing, hide it
			marker.hideInfoWindow();
			markerWindowShown = false;
		} else {  // window not showing, see if we should show it
			if (isInRange(marker) && pin.isLocked()) {
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
		// TODO this
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
		Log.d(res + "", "cccccc");
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
	 * Adds a pin to the map at the coordinates given
	 * 
	 * @param dialog a reference to the fragment this is listening on
	 * @param lat the latitude to put the pin
	 * @param lng the longitude to put the pin
	 */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, LatLng coord, String message) {
    	//TODO: we should make the post fragment just return a location and message
    	//      which is passed to postPin
    	//Pin pin = dbs.postPin(coord, message);
    	Pin pin = new Pin(coord, null, message);
        addPin(pin);
    }
    
    /**************** location listener ****************/
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
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

		//updateMap();
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
		}
		
		/*
		 * First remove old pins that aren't in view now
		 */
		for(Iterator<Marker> iter = geoposts.keySet().iterator(); iter.hasNext(); ){
			Marker m = iter.next();
			Pin p = geoposts.get(m);
			if (!pins.contains(p)){
				// m is no longer in our scope
				m.remove();
				geoposts.remove(m);
			}
		}
		
		/*
		 * Now add new pins that weren't drawn before
		 */
		Collection<Pin> pinvalues = geoposts.values();
		for (Pin p : pins){
			if (!pinvalues.contains(p)){
				// this will add p to geoposts
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
			
			Log.d("updateMap", "got pins");
			
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
