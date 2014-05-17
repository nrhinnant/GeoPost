package edu.washington.geopost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		// Get variables from the intent
	    Intent intent = getIntent();
	    String name = intent.getStringExtra("edu.washington.geopost.USERNAME");
	    int numPosted = intent.getIntExtra("edu.washington.geopost.NUM_POSTED", 0);
	    int numViewed = intent.getIntExtra("edu.washington.geopost.NUM_VIEWED", 0);
	    
	    // Display username
	    TextView nameView = new TextView(this);
	    nameView = (TextView) findViewById(R.id.username); 
	    nameView.setText(name);
	    
	    // Display num posted
	    TextView numPostedView = new TextView(this);
	    numPostedView = (TextView) findViewById(R.id.num_posted); 
	    numPostedView.setText("Posted: " + numPosted);
	    
	    // Display num viewed
	    TextView numViewedView = new TextView(this);
	    numViewedView = (TextView) findViewById(R.id.num_viewed); 
	    numViewedView.setText("Unlocked: " + numViewed);
	}
}
