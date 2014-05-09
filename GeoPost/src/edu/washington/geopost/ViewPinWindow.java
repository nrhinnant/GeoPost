package edu.washington.geopost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class ViewPinWindow implements InfoWindowAdapter {
	
	private final View myContentsView;
	
	public ViewPinWindow(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		myContentsView = inflater.inflate(R.layout.view_pin_window, null);
	}

	/**
	 * Customizes content of the window
	 * Sets an author text field and a message text field
	 */
	@Override
	public View getInfoContents(Marker arg0) {
		TextView author = (TextView) myContentsView.findViewById(R.id.author);
		author.setText("anonymous");
		TextView message = (TextView) myContentsView.findViewById(R.id.message);
		message.setText("this is a sample message");
		return myContentsView;
	}

	/**
	 * Allows for providing a view for the info window
	 * Returns null, so the default view is used. 
	 */
	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
