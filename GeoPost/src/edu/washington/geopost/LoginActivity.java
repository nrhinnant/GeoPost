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

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	
	private final String PARSE_APP_ID = ""; 		// Change this to your Parse application id
	private final String PARSE_CLIENT_KEY = ""; 	// Change this to your Parse client key
	private final String FACEBOOK_APP_ID = "";		// Change this to your Facebook application id
	
	private Button loginButton;
	private Dialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
        ParseFacebookUtils.initialize(FACEBOOK_APP_ID);

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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// For single sign-on
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
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
					Log.d(MainActivity.TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(MainActivity.TAG,
							"User signed up and logged in through Facebook!");
					showMainActivity();
				} else {
					Log.d(MainActivity.TAG,
							"User logged in through Facebook!");
					showMainActivity();
				}
			}
		});
	}
	
	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	
}
