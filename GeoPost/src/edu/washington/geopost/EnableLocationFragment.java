package edu.washington.geopost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class EnableLocationFragment extends DialogFragment {
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it
     * as well as coordinates and a message */
    public interface EnableLocationDialogListener {
        public void onEnableLocationPositiveClick(DialogFragment dialog);
    }
    
    // Use this instance of the interface to deliver action events
    EnableLocationDialogListener listener;
	
    /**
     *  Override the Fragment.onAttach() method to instantiate the EnableLocationDialogListener
     *  
     *  @param activity the activity that will add a listener
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (EnableLocationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    
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
        
        // Sets the content dialog to be dialog_post
        // Also sets a Post and Cancel button
        builder.setTitle("Location services disabled")
        	   .setMessage("GeoPost needs access to your location. Please turn on Location Services")
               .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       listener.onEnableLocationPositiveClick(EnableLocationFragment.this);
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
