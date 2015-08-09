package com.sample.tourguide;

import android.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;
import com.robotium.solo.*;

import com.sample.tourguide.activity.TourGuideActivity;
import com.sample.tourguide.parser.FourSquareDataParser;
import com.sample.tourguide.service.LocationTracker;


/**
 * Created by keyur on 09-08-2015.
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2{
    private Solo  solo;
    private SystemUtils mUtils;
    private String mTestURL="https://api.foursquare.com/v2/venues/explore?client_id=XWLGRC03I0EFFEFCSIKSRTDVN5IK5Y1JMBR4DOWOAK2TC25C&client_secret=K1QFNQVG0JSK22B0ASDZXRTDT2FEVBTTQS2KSESFRWVW1153&v=20150711&limit=100&ll=-37.8732039,144.9902795";
    public ApplicationTest () {

        super(TourGuideActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp ();
        solo = new Solo(getInstrumentation(), getActivity());

    }
    @Override
    protected void tearDown() throws Exception{
        try {
            solo.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        getActivity().finish ();
        super.tearDown ();
    }
    public void testShouldDisplayActivity() throws Exception {
        // check that we have the right activity
        solo.waitForActivity (solo.getCurrentActivity ().toString ());
        solo.assertCurrentActivity ("wrong activity", TourGuideActivity.class);
        //assertFalse(solo.getCurrentActivity ().getActionBar ().);
        Fragment myMap=(solo.getCurrentActivity ().getFragmentManager ().findFragmentById (R.id.map));
        assertTrue (myMap.isVisible ());
    }

    public void testJSONDataLoad() throws Exception {

        FourSquareDataParser.JSONFeedParserTest ( mTestURL);
        boolean expected = true;
        //boolean actual =solo.waitForText(getActivity().getResources().getString(R.string.success));
        boolean actual =solo.waitForLogMessage("JSONParseSuccess");
        assertEquals ("success", expected, actual);

    }
    public void testLocationService() throws Exception {

        LocationTracker.LocationTest ();
        boolean expected = true;

        // boolean actual =solo.waitForText(getActivity().getResources().getString(R.string.success));
        boolean actual =solo.waitForLogMessage("canGetLocationSuccess");
        assertEquals("success", expected, actual);
    }





}