package edu.washington.geopost;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * 
 * This class initializes the parse and facebook app settings
 * 
 * @author Megan Drasnin
 *
 */

public class Application extends android.app.Application {

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

    ParseObject.registerSubclass(ParsePin.class);
    
    // Enter your own parse app id, parse client id, and facebook app id in strings.xml
    Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_id));
    ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
  }


}
