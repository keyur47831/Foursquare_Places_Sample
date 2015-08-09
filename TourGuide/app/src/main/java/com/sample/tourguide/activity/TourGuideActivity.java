package com.sample.tourguide.activity;



import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sample.tourguide.Define;
import com.sample.tourguide.fragment.FragmentDrawer;
import com.sample.tourguide.model.LocationModel;
import com.sample.tourguide.parser.FourSquareDataParser;
import com.sample.tourguide.service.LocationTracker;
import com.sample.tourguide.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by keyur on 08-08-2015.
 */

public class TourGuideActivity extends AppCompatActivity implements OnMapReadyCallback,FragmentDrawer.FragmentDrawerListener
{
    //UI related declarations
    protected Toolbar mToolbar;
    protected FragmentDrawer mDrawerFragment;
    protected GoogleMap mGoogleMap;
    //To store our current location
    Location mCurrentLocation;
    //List of nearby places
    List<LatLng> mNearByLocation=new ArrayList<> ();
    //Location service
    private LocationTracker mLocationTracker;
    //URL to fetch data from foursquare
    private String mURL;
    //List of all the places nearby
    //Refer model class
    private List <LocationModel> mLocationData=new ArrayList<> ();
    //For logs
    private static final String TAG = TourGuideActivity.class.getSimpleName ();
    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        //Init the toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar (mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled (true);
       //Attach the fragment
        mDrawerFragment = (FragmentDrawer)getSupportFragmentManager ().
                findFragmentById (R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp
                (R.id.fragment_navigation_drawer, (DrawerLayout) findViewById (R.id.drawer_layout), mToolbar);
        mDrawerFragment.setDrawerListener (this);


        //Check if GPS is enabled or not
        if (!AppController.getInstance ().isGPSEnable ())
        {
            //Ask the user to enable GPS
            showGPSDisabledAlertToUser();
           return;
        }
        else
        {
            //Check if Internet is enabled or not
           if(AppController.getInstance ().isDataConnAvailable ()) {
               //We can now init our location service
               //and loading google map and location tracker
               mLocationTracker=new LocationTracker(LocationChanged);
               // Loading map
               initMap ();
           }
        else
           {    //Ask the user to enable Internet access
               showInternetSettingAlert ();
              return;
           }

    }}
    /*
    * Function to start init the map fragment
     */
    private void initMap () {
        //We will run this task in background to avoid blocking the UI
        //thread
        if (mGoogleMap == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onStop () {
        super.onStop ();
        //Release the location service
        //since our activity is about to destroy
        if(mLocationTracker!=null)
            mLocationTracker.stopUsingGPS ();
    }

   /*
   * Override to handle the item selected
    */
    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position)
        {
            //This might happen that user has turned off the gps or data connection
            //once the activity is launched so we check here
            //second method is to register for GPS and Network changes intent
            case 0:

                if(AppController.getInstance ().isDataConnAvailable ()&& AppController.getInstance ().isGPSEnable ()) {
                    //clear the map and load again
                    //<testcase> : Enable the GPS and come back to our application
                    //the service is not started
                    if(mLocationTracker==null)
                    mLocationTracker=new LocationTracker(LocationChanged);
                   mGoogleMap=null;
                    initMap ();
                }
                else {
                    //Check if Internet is enabled or not
                    if (!AppController.getInstance ().isDataConnAvailable ()) {
                        showInternetSettingAlert ();
                    } else
                    {    //Ask the user to enable Internet access
                        showGPSDisabledAlertToUser ();

                    }
                }
                break;
            case 1:
                //Display markers on the map
                if(AppController.getInstance ().isDataConnAvailable ()&& AppController.getInstance ().isGPSEnable ()) {
                    if(mLocationTracker==null)
                        mLocationTracker=new LocationTracker(LocationChanged);
                    DisplayMarkers ();
                }
                else {
                    //Check if Internet is enabled or not
                    if (!AppController.getInstance ().isDataConnAvailable ()) {
                        showInternetSettingAlert ();
                    } else
                    {    //Ask the user to enable Internet access
                        showGPSDisabledAlertToUser ();

                    }
                }
                break;
            case 2 :
                if(AppController.getInstance ().isDataConnAvailable ()&& AppController.getInstance ().isGPSEnable ()) {
                    if(mLocationTracker==null)
                        mLocationTracker=new LocationTracker(LocationChanged);
                    //Check if Marker data available
                    if(mLocationData==null)
                        DisplayMarkers();
                    //Display path on map
                    DisplayPath();
                    //Display path on map

                }
                else {
                    //Check if Internet is enabled or not
                    if (!AppController.getInstance ().isDataConnAvailable ()) {
                        showInternetSettingAlert ();
                    } else {    //Ask the user to enable Internet access
                        showGPSDisabledAlertToUser ();
                        return;
                    }
                }
                break;
        }

    }
    /*
     * This is callback from our service when the location changes
     */
    private LocationTracker.onPositionChanged LocationChanged = new LocationTracker.onPositionChanged(){
        @Override
        public  void getNewLocation(Location location)
        {
            mCurrentLocation=location;
            UpdateMap();
        }

    };
    /*
     * This is callback once the map is loaded in background
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap=map;
        if(mCurrentLocation!=null) {
            UpdateMap ();
        }
    }
    /*
    * Display Alert to User that InternetSetting is disabled
     */
    public  void showInternetSettingAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString (R.string.internet_connection));

        // Setting Dialog Message
        alertDialog.setMessage(getString (R.string.no_internet));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString (R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity (intent);
                dialog.dismiss ();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.alert_dialog_cancel),new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int which){
        dialog.cancel();
        }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    /*
    * This function is used to display the path connecting the markers
     */
    public void DisplayPath()
    {

        //Error Checking
        if(mNearByLocation.size ()>0 && mLocationData.size ()>0)
        {
            List<LatLng> refNearbyData=mNearByLocation;
            Log.d (TAG,"keyur" +refNearbyData.size () );
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color (Color.BLUE);
            polylineOptions.width (5);
            //The first point to start is current location
            polylineOptions.add (new LatLng (mCurrentLocation.getLatitude (), mCurrentLocation.getLongitude ()));
            //Next will be the location nearby to current location
            //the data received from json
            //is sorted based on distance from current location
            LocationModel newStart =mLocationData.get(0);
            polylineOptions.add (new LatLng (newStart.getLatitude (), newStart.getLongitude ()));
            mGoogleMap.addPolyline (polylineOptions);
            //Now our new start  location is closest point
            //from current location so first we sort with new start
            //and then remove it. So we get the next nearmost
            //from new start
                Collections.sort (refNearbyData, LocationModel.createComparator (new LatLng (newStart.getLatitude (), newStart.getLongitude ())));
            //Remove this as we have already
            //connected it from current location
            refNearbyData.remove (0);
                polylineOptions.add (refNearbyData.get (0));
                mGoogleMap.addPolyline (polylineOptions);
            //Now we loop from mNearByLocation and checking
            //which is the next near most
            // for the given point.

                while(refNearbyData.size()!=1)
                {

                    Log.d (TAG,"keyur" +refNearbyData.size () );
                    LatLng newStartLatLng=new LatLng (refNearbyData.get(0).latitude,refNearbyData.get(0).longitude);
                    refNearbyData.remove (0);
                    //For each location, we will sort the nearby list
                    //again the find the nearmost
                    //point
                    Collections.sort (refNearbyData, LocationModel.createComparator (newStartLatLng));
                   polylineOptions.add (refNearbyData.get(0));
                   mGoogleMap.addPolyline (polylineOptions);
                    //Did We reach end of list ??
                    if(refNearbyData.size ()==1)
                    {
                        polylineOptions.add (new LatLng (mCurrentLocation.getLatitude (), mCurrentLocation.getLongitude ()));
                        mGoogleMap.addPolyline (polylineOptions);
                        break;
                    }
                    //free the object
                    newStartLatLng=null;
                }


        }



    }

    /*
      Provides a simple way of getting a device's location.
      Gets the best and most recent location currently available, which may be null
      in rare cases when a location is not available.
     */
    public void UpdateMap() {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
            mGoogleMap.clear ();
            LatLng mCurrentLatLng=new LatLng(mCurrentLocation.getLatitude (),mCurrentLocation.getLongitude ());
            MarkerOptions marker = new MarkerOptions ().position(mCurrentLatLng).title(getString (R.string.here));
           // adding marker
            marker.icon(BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_BLUE));
            mGoogleMap.addMarker (marker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target (mCurrentLatLng).zoom (12).build ();
        mGoogleMap.setMyLocationEnabled (true);
           mGoogleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
    }
    /*
     Function to set the URL for foursquare
     and will get the data about nearby places
     */
    public void DisplayMarkers()
    {
        //All the string contants are defines in
        //in single location for ease
        if(mCurrentLocation!=null) {
            mURL = Define.FOUR_SQUARE_URL;
            mURL += "&ll=" + mCurrentLocation.getLatitude () + "," + mCurrentLocation.getLongitude ();
            FourSquareDataParser mJsonParser = new FourSquareDataParser (mURL, mListner);
            mJsonParser.loadJson ();
        }
    }
    /*
      Function to all the nearby places marker
     */
    public void AddMarkers()
    {    //First clear the old data
        if(mNearByLocation!=null)
          mNearByLocation.clear ();
        for(int i=0;i<mLocationData.size ();i++) {
            LocationModel mItem = mLocationData.get (i);
            MarkerOptions marker = new MarkerOptions ().position (new LatLng (mItem.getLatitude (),mItem.getLongitude ()));
            marker.icon (BitmapDescriptorFactory.fromResource (R.drawable.ic_pin));
            marker.title (mItem.getName ());
            marker.snippet (mItem.getAddress ());
            mGoogleMap.addMarker (marker);
            mNearByLocation.add (new LatLng (mItem.getLatitude (), mItem.getLongitude ()));
        }


    }
    /*
      This function is called when JSON data loading is completed
      in background by volley
     */
    private FourSquareDataParser.onJsonParseCompleted mListner= new FourSquareDataParser.onJsonParseCompleted(){

        @Override
        public void onParseSuccess(List<LocationModel> feed) {
            if(mLocationData!=null) {
                mLocationData.clear ();
            }
            mLocationData=feed;
            AddMarkers();

        }

        @Override
        public void onParseFailure() {
            //Display error toast
            Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
        }
    };
    /*
     This will show alert box for user to enable GPS
     */
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString (R.string.no_GPS))
                .setCancelable (false)
                .setPositiveButton(getString (R.string.GPS_connection),
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }



}


