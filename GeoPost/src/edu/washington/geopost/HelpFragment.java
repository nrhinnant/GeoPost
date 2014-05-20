package edu.washington.geopost;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public class HelpFragment extends DialogFragment {
	/**
     * Create the dialog view. 
     * Uses the dialog_post layout and sets listeners on button clicks. 
     * Dispatch to listeners on a positive button click. 
     * 
     * @param savedInstanceState parameters passed into this method. 
     * 			Should include a latitude and longitude
     * @return the dialog view
     */
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Sets the content dialog to be dialog_help
        // Also sets an OK button
        builder.setView(inflater.inflate(R.layout.dialog_help, null))
        	   .setTitle(R.string.help_title)
               .setPositiveButton(R.string.ok_message, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   // User presesed "ok"
                   }
               });       
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
