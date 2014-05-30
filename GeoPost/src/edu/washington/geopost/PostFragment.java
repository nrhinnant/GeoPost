package edu.washington.geopost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * A post fragment is a window displayed on an application. 
 * It contains a positive and negative button and a way to enter
 * information that will be transmitted to listeners on clicking
 * of the positive button. 
 * 
 * @author Ethan
 *
 */
public class PostFragment extends DialogFragment implements OnClickListener {
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it
     * as well as coordinates and a message */
    public interface PostDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String message, Bitmap photo);
    }
    
    // Use this instance of the interface to deliver action events
    PostDialogListener listener;
    ImageView imagePreview;
    private Bitmap finalPhoto;
	
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
		Log.d("CAM", "oncreatedialog");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        // Sets the content dialog to be dialog_post
        // Also sets a Post and Cancel button
        
        View view = inflater.inflate(R.layout.dialog_post, null);
        ImageButton cam = (ImageButton) view.findViewById(R.id.imageButton1);
        cam.setOnClickListener(this);
        imagePreview = (ImageView) view.findViewById(R.id.imageView1);
        
        builder.setView(view)
               .setPositiveButton(R.string.button_message, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       listener.onDialogPositiveClick(PostFragment.this, getMessage(), getPhoto());
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
	
	/**
	 * Called when camera activity finishes. 
	 * Saves the image taken by the user
	 * 
	 * @param requestCode who the result came from
	 * @param resultCode result code from child activity
	 * @param data the data returned from the camera intent
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("CAM", "on activity result for cam");
	    if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        imagePreview.setVisibility(View.VISIBLE);
	        imagePreview.setImageBitmap(imageBitmap);
	        finalPhoto = imageBitmap;
	    }
	}
	
	/**
	 * Returns the photo taken by the user
	 * 
	 * @return the photo taken by the user
	 */
	private Bitmap getPhoto() {
		Log.d("PHOTO", "getPhoto");
		if (finalPhoto != null) {
			Log.d("PostPin", "Yes, took photo");
			return finalPhoto;
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieve and return the message entered by the user
	 * 
	 * @return the message entered by the user
	 */
	private String getMessage() {
		Log.d("PHOTO", "getMessage");
		EditText e = (EditText) getDialog().findViewById(R.id.post_text);
		return e.getText().toString();
	}

	/**
	 * Called on click of the camera button
	 * Starts an activity to take a picture
	 * 
	 * @param v a view of the the clicked button
	 */
	@Override
	public void onClick(View v) {
		Log.d("CAM", "on cam click");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, 1);
	    }
	}
	
}
