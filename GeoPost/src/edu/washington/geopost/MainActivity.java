package edu.washington.geopost;

import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 
						  implements OnMarkerClickListener, 
									 LocationListener, 
									 PostFragment.PostDialogListener {
	
	private final double RANGE_RADIUS = 1.0;
	private LocationManager locationManager;
	private String provider;
	private GoogleMap map;
	private boolean markerWindowShown;
	private Map<String, Pin> pinIdToPin;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded(); 
        
        // Set the pin pop up windows to use the ViewPinWindow class
        map.setInfoWindowAdapter(new ViewPinWindow(this));
        
        markerWindowShown = false;
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        
        // Initialize provider (this provider doesn't always work)
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        
        map.setMyLocationEnabled(true);
        // Turn off the rotation capability of the map
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.setOnMarkerClickListener(this);
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
     * @param id the id of the marker (stored in marker.title)
     * @param lat the latitude to put the pin
     * @param lng the longitude to put the pin
     */
    private void addPin(String id, double lat, double lng){
    	map.addMarker(new MarkerOptions()
    	.title(id)
    	.position(new LatLng(lat, lng)));
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
     */
	@Override
	public boolean onMarkerClick(Marker marker) {
		// Note: marker.isInfoWindowShown() has a bug, don't use it
		if (markerWindowShown) {
			marker.hideInfoWindow();
			markerWindowShown = false;
		} else {
			if (isInRange(marker)) {
				marker.showInfoWindow();
				markerWindowShown = true;
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
		/*
		Location l = getLastKnownLocation();
		if (l == null) {
			Toast toast = Toast.makeText(getApplicationContext(), "Could not find your location", 
										Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		double userLat = l.getLatitude();
		double userLng = l.getLongitude();
		double pinLat = 
		double pinLng = 
		double res = Math.sqrt(Math.pow(userLat - pinLat, 2) + Math.pow(userLng - pinLng, 2.0);
		return res <= RANGE_RADIUS;
		*/
		
		return true;
	}
	
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
    public void onDialogPositiveClick(DialogFragment dialog, double lat, double lng, Pin pin) {
        addPin("pinId", lat, lng);
    }

	// Inherited by LocationListener 
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
}
