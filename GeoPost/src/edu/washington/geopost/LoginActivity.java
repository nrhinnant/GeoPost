package edu.washington.geopost;

import android.app.Activity;

import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
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
	 * Displays the login button
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FB", "onCreate");

		setContentView(R.layout.activity_login);

		Log.d("FB", "setcontentview");

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		Log.d("FB", "Before checking for previous user");
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
		Log.d("FB", "onActivityResult");
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		if (!isNetworkAvailable()) {
			Toast toast = Toast.makeText(getApplicationContext(), "Network unavailable", 
					Toast.LENGTH_LONG);
			toast.show();
		}
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
	            	saveUserInfo();
	            	showMainActivity();
	            } else {
	            	Log.d(MainActivity.TAG, "User logged in through Facebook!");
	            	saveUserInfo();
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
	 * Saves the user's Facebook ID and name from Facebook in the Parse database.
	 */
	private void saveUserInfo() {
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {

			Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					// handle response
					if (user != null) {

						ParseUser currentUser = ParseUser.getCurrentUser();
						currentUser.put("name", user.getName());
						currentUser.put("facebookID", user.getId());
						currentUser.saveInBackground();


					} else if (response.getError() != null) {
						Log.d(MainActivity.TAG, response.getError().getErrorMessage());

					}
				}
			});
			request.executeAsync();
		}
	}
	
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	
}
