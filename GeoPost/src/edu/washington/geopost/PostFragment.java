package edu.washington.geopost;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * A post fragment is a window displayed on an application. 
 * It contains a positive and negative button and a way to enter
 * information that will be transmitted to listeners on clicking
 * of the positive button. 
 * 
 * @author Ethan Goldman-Kirst
 *
 */
public class PostFragment extends DialogFragment {
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it
     * as well as coordinates and a message */
    public interface PostDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String message);
    }
    
    // Use this instance of the interface to deliver action events
    PostDialogListener listener;
	
    /**
     *  Override the Fragment.onAttach() method to instantiate the PostDialogListener
     *  
     *  @param activity the activity that will add a listener
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (PostDialogListener) activity;
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
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Sets the content dialog to be dialog_post
        // Also sets a Post and Cancel button
        builder.setView(inflater.inflate(R.layout.dialog_post, null))
               .setPositiveButton(R.string.button_message, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       listener.onDialogPositiveClick(PostFragment.this, getMessage());
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        //mImageView.setImageBitmap(imageBitmap);
	    }
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	    	startActivityForResult(takePictureIntent, 1);
	    }
	}
	
	/**
	 * Retrieve and return the message entered by the user
	 * @return the message entered by the user
	 */
	private String getMessage() {
		EditText e = (EditText) getDialog().findViewById(R.id.post_text);
		return e.getText().toString();
	}
	
}
