package edu.washington.geopost;

import android.app.Activity;

import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * 
 * The user logs in from this activity.
 * 
 * @author Megan Drasnin
 *
 */

public class LoginActivity extends Activity {

	
	private Button loginButton;
	private Dialog progressDialog;
	
	/**
	 * Registers the app with Parse and Facebook and displays the login button.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		// Enter your own parse app id, parse client id, and facebook app id in strings.xml
		ParseObject.registerSubclass(ParsePin.class);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_id));
        ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the map activity
			showMainActivity();
		}
	}
	
	/**
	 * Finishes Facebook authentication.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	/**
	 * Logs in the user when they press the login button.
	 */
	private void onLoginButtonClicked() {
	    LoginActivity.this.progressDialog = ProgressDialog.show(
	            LoginActivity.this, "", "Logging in...", true);
	    // TODO: Change permissions
	    List<String> permissions = Arrays.asList("public_profile", "email", "user_friends" );
	    ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
	        @Override
	        public void done(ParseUser user, ParseException err) {
	            LoginActivity.this.progressDialog.dismiss();
	            if (user == null) {
	            	Log.d(MainActivity.TAG, "User cancelled the Facebook login.");
	            } else if (user.isNew()) {
	            	Log.d(MainActivity.TAG, "User signed up and logged in through Facebook!");
	            	saveUsersName();
	            	showMainActivity();
	            } else {
	            	Log.d(MainActivity.TAG, "User logged in through Facebook!");
	            	saveUsersName();
	            	showMainActivity();
	            }
	        }
	    });
	}

	/**
	 * Shows the map activity.
	 */
	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Saves the user's name from Facebook in the Parse database.
	 */
	private void saveUsersName() {
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {

			Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					// handle response
					if (user != null) {

						ParseUser currentUser = ParseUser.getCurrentUser();
						currentUser.put("name", user.getName());
						currentUser.saveInBackground();


					} else if (response.getError() != null) {
						Log.d(MainActivity.TAG, response.getError().getErrorMessage());

					}
				}
			});
			request.executeAsync();
		}
	}

	
}
