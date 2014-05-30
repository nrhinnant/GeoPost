package edu.washington.geopost;

import android.app.Activity;

import java.util.ArrayList;
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
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;


/**
 * This activity provides the functionality for users to log in to the app.
 * 
 * @authors Megan Drasnin, Andrew Repp
 */

public class LoginActivity extends Activity {
	private Button loginButton;
	private Dialog progressDialog;
	
	/**
	 * Displays the login button and skips to the main app if the user is
	 * already logged in.
	 * @param savedInstanceState the data saved by the app the last time it
	 *                           shutdown, or null if there is no such data
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			// Hooks up the login button to its listener
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		// Check if there is a currently logged in user and they are linked to
		// a Facebook account. If they are, skip to the main map activity.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			showMainActivity();
		}
	}
	
	/**
	 * Finishes Facebook authentication.
	 * @param requestCode the code identifying who made the request
	 * @param resultCode the code containing information on the result
	 * @param data the data returned by the activity, if any
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
		Log.d("DEBUG", "onActivityResult finished Authentication");
		if (!isNetworkAvailable()) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Network unavailable", Toast.LENGTH_LONG);
			toast.show();
		}
	}
	
	/**
	 * Logs in the user when they press the login button.
	 */
	private void onLoginButtonClicked() {
	    LoginActivity.this.progressDialog = ProgressDialog.show(
	            LoginActivity.this, "", "Logging in...", true);
	    List<String> permissions = Arrays.asList("public_profile", "user_friends");
	    
	    // Attempt to log the user in. If the login is successful, save
	    // important data for later and proceed to the main app.
	    ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
	        @Override
	        public void done(ParseUser user, ParseException err) {
	            LoginActivity.this.progressDialog.dismiss();
	            if (user == null) { // error
	            	Log.d("DEBUG", "User cancelled the Facebook login.");
	            } else { // success
	            	if (user.isNew()) {
	            		Log.d("DEBUG", "User signed up and logged in through"
	            				+ "Facebook!");
	            	} else {
	            		Log.d("DEBUG", "User logged in through Facebook!");
	            	}
	            	saveUserInfo();
	            	getFriends();
	            	showMainActivity();
	            }
	        }
	    });
	}

	/**
	 * Displays the map activity.
	 */
	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	/**
	 * Saves the user's Facebook ID and name from Facebook in the Parse 
	 * database.
	 */
	private void saveUserInfo() {
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			// Make the request to Facebook's Graph to get the data
			Request request = Request.newMeRequest(session, 
					new Request.GraphUserCallback() {
				// handle response
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) { // successfully got data about user
						ParseUser currentUser = ParseUser.getCurrentUser();
						currentUser.setUsername(user.getName());
						currentUser.put("facebookID", user.getId());
						currentUser.saveEventually();
					} else if (response.getError() != null) { // error to display
						Log.d(MainActivity.TAG, 
								response.getError().getErrorMessage());
					}
				}
			});
			request.executeAsync();
		}
	}
	
	/**
	 * Saves the user's Facebook friends in the Parse database.
	 */
	private void getFriends() {
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			// Make the request to Facebook's Graph to get the data
			Request request = Request.newMyFriendsRequest(session,
					new Request.GraphUserListCallback() {
				@Override
				public void onCompleted(List<GraphUser> users,
						Response response) {
					if (users != null) { // successfully got user's friends
						List<String> friendsList = new ArrayList<String>();
						for (GraphUser user : users) {
							friendsList.add(user.getId());
						}
						
						// Construct a ParseUser query that will find friends
						// whose Facebook IDs are contained in the current
						// user's friend list.
						ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
						friendQuery.whereContainedIn("facebookID", friendsList);

						// find will return a a list of ParseUsers that are 
						// friends with the current user
						List<ParseUser> friendUsers = null;
						try {
							friendUsers = friendQuery.find();
						} catch (ParseException e) { // error finding friends
							Log.d(MainActivity.TAG, "Could not find facebook "
									+ "friends.");
							return;
						}
						
						// Save the current user's Facebook friends in the 
						// database in the user's friends relation
						ParseUser currentUser = ParseUser.getCurrentUser();
						ParseRelation<ParseUser> friendsRelation = 
								currentUser.getRelation("friends");
						for (ParseUser friend : friendUsers) {
							friendsRelation.add(friend);
						}
						currentUser.saveEventually();
					}
				}
			});
			request.executeAsync();
		}
	}
	
	/**
	 * Determines if the phone is connected to a network.
	 * @return true if the phone is connected to any network, false otherwise
	 */
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
