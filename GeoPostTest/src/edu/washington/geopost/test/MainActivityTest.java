package edu.washington.geopost.test;

import edu.washington.geopost.MainActivity;
import android.test.ActivityInstrumentationTestCase2;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity mFirstActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MainActivity mFirstAcitivty = getActivity();
    }
    
    public void testPreconditions() {
        assertNotNull("mFirstTestActivity is null", mFirstActivity);
    }
    
    public void testMyFirstTestTextView_labelText() {
        assertEquals(1, 0);
    }
}
