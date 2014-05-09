package edu.washington.geopost;

import java.util.List;

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
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity implements OnMarkerClickListener, 
													LocationListener {
	private LocationManager locationManager;
	private String provider;
	private GoogleMap map;
	private boolean markerWindowShown;
	
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
        
        Location l = getLastKnownLocation();
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);
        addPin("TEST", 0, 0);
        //addPin("TEST", l.getLatitude(), l.getLongitude());
    }
    
    // Loops through available providers and finds one that is not null with the best accuracy
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
    
    private void addPin(String title, double lat, double lng){
    	map.addMarker(new MarkerOptions()
    	.title(title)
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
     */
	@Override
	public boolean onMarkerClick(Marker marker) {
		if (markerWindowShown) {
			marker.hideInfoWindow();
			markerWindowShown = false;
		} else {
			marker.showInfoWindow();
			markerWindowShown = true;
		}
		return true;
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
