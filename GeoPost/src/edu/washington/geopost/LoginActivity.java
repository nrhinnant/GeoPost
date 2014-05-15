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
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	
	private Button loginButton;
	private Dialog progressDialog;
	
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
	            	Log.d(MainActivity.TAG, "Uh oh. The user cancelled the Facebook login.");
	            } else if (user.isNew()) {
	            	Log.d(MainActivity.TAG, "User signed up and logged in through Facebook!");
	            	showMainActivity();
	            } else {
	            	Log.d(MainActivity.TAG, "User signed up and logged in through Facebook!");
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
