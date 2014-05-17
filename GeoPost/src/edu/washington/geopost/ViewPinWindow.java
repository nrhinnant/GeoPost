package edu.washington.geopost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * A ViewPinWindow displays a view on clicking of a marker on
 * a GoogleMap. 
 * 
 * @author Ethan Goldman-Kirst
 *
 */
public class ViewPinWindow implements InfoWindowAdapter {
	
	private final View myContentsView;
	
	/**
	 * Constructor for a ViewPinWindow
	 * Sets the layout to be view_pin_window
	 * 
	 * @param context the layout to put the ViewPinWindow on
	 * 		(for this project, this should be MainActivity)
	 */
	public ViewPinWindow(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		myContentsView = inflater.inflate(R.layout.view_pin_window, null);
	}

	/**
	 * Customizes content of the window
	 * Sets an author text field and a message text field
	 * 
	 * @param marker the clicked marker
	 * @return the updated view for the window
	 */
	@Override
	public View getInfoContents(Marker marker) {
		TextView author = (TextView) myContentsView.findViewById(R.id.author);
		TextView message = (TextView) myContentsView.findViewById(R.id.message);
		
		// this field is filled with the user who posted the pin
		author.setText(marker.getSnippet());
		
		// this field is filled with the message of the pin
		message.setText(marker.getTitle());
		
		return myContentsView;
	}

	/**
	 * Allows for providing a view for the info window
	 * Returns null, so the default view is used. 
	 * 
	 * @param marker the clicked marker
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

}
