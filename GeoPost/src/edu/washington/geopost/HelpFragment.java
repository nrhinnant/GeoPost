package edu.washington.geopost;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

/**
 * A HelpFragment displays a help page. 
 * The page consists of a title, a main view, and an "OK" button
 * 
 * @author Ethan
 *
 */
public class HelpFragment extends DialogFragment {
	/**
     * Create the dialog view. 
     * Uses the dialog_help layout and sets a title and positive button
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
