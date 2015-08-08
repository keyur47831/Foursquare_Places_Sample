package com.sample.tourguide.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sample.tourguide.Define;
import com.sample.tourguide.fragment.FragmentDrawer;
import com.sample.tourguide.model.LocationModel;
import com.sample.tourguide.parser.FourSquareDataParser;
import com.sample.tourguide.service.LocationTracker;
import com.tourguide.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class TourGuideActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener
{
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    private GoogleMap googleMap;
    protected static final String TAG = TourGuideActivity.class.getSimpleName ();
    Location mCurrentLocation;
    List<LatLng> mNearByLocation=new ArrayList<> ();
    private LocationTracker mLocationTracker;
    private String mURL;
    private List <LocationModel> mLocationData=new ArrayList<> ();
    private boolean mDisplayPath=false;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mLocationTracker=new LocationTracker(this,LocationChanged);
        setSupportActionBar (mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled (true);

            drawerFragment = (FragmentDrawer)
                    getSupportFragmentManager ().findFragmentById (R.id.fragment_navigation_drawer);
            drawerFragment.setUp (R.id.fragment_navigation_drawer, (DrawerLayout) findViewById (R.id.drawer_layout), mToolbar);
            drawerFragment.setDrawerListener (this);
           // displayView (0);
        try {

            // Loading map
           initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap ();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText (getApplicationContext (),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mCurrentLocation=mLocationTracker.getLocation ();

    }
    @Override
    protected void onStop() {
        super.onStop ();

    }
    @Override
    public void onResume()
    {
        super.onResume ();

    }
    /*
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected (item);
    } */
    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position)
        {
            case 0:
                initilizeMap ();
                break;
            case 1:
                //markers
                DisplayMarkers();
                break;
            case 2 :
                //drawpolygons
                DisplayPath();
                break;



        }

    }
    private LocationTracker.onPositionChanged LocationChanged = new LocationTracker.onPositionChanged(){
        @Override
        public  void getNewLocation(Location location)
        {
            mCurrentLocation=location;
            UpdateMap();
        }

    };



   /* private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment ();
                title = getString(R.string.nav_item_home);
                break;
            case 1:
                fragment = new ShowNearbyFragment ();
                title = getString(R.string.nav_item_nearby);
                break;
            case 2:
                fragment = new ShowTourMapFragment ();
                title = getString(R.string.nav_item_tour_route);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    } */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.GPS_connection));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.no_GPS));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString (R.string.action_settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity (intent);
                dialog.dismiss ();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getString (R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
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
    public void DisplayPath()
    {
        if(mNearByLocation!=null)
        {

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color (Color.RED);
            polylineOptions.width (5);
            polylineOptions.add (new LatLng (mCurrentLocation.getLatitude (), mCurrentLocation.getLongitude ()));
            LocationModel newStart =mLocationData.get(0);
            polylineOptions.add (new LatLng (newStart.getLatitude (), newStart.getLongitude ()));
            googleMap.addPolyline (polylineOptions);
           // mNearByLocation.remove ()
                Collections.sort (mNearByLocation, LocationModel.createComparator (new LatLng (newStart.getLatitude (), newStart.getLongitude ())));
                 mNearByLocation.remove (0);
                polylineOptions.add (mNearByLocation.get (0));
                googleMap.addPolyline (polylineOptions);
                //mNearByLocation.remove (0);

                while(mNearByLocation.size()!=1)
                {
                    Log.d (TAG,"--"+mNearByLocation.size ());
                    LatLng newStartLatLng=new LatLng (mNearByLocation.get(0).latitude,mNearByLocation.get(0).longitude);
                    mNearByLocation.remove (0);
                    Collections.sort (mNearByLocation, LocationModel.createComparator (newStartLatLng));
                   polylineOptions.add (mNearByLocation.get(0));
                   googleMap.addPolyline (polylineOptions);

                    if(mNearByLocation.size ()==1)
                    {
                        polylineOptions.add (new LatLng (mCurrentLocation.getLatitude (), mCurrentLocation.getLongitude ()));
                        googleMap.addPolyline (polylineOptions);
                        break;
                    }
                }


        }


    }

    public void UpdateMap() {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

            //initilizeMap();
          googleMap.clear ();

            LatLng mCurrentLatLng=new LatLng(mCurrentLocation.getLatitude (),mCurrentLocation.getLongitude ());
            MarkerOptions marker = new MarkerOptions ().position(mCurrentLatLng).title("Hello Maps ");

// adding marker
            marker.icon(BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_BLUE));
            googleMap.addMarker (marker);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(mCurrentLatLng).zoom(12).build();
            googleMap.setMyLocationEnabled (true);
            googleMap.animateCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));

        }
    public void DisplayMarkers()
    {
        mURL=Define.FOUR_SQUARE_URL;
        mURL+="&ll="+mCurrentLocation.getLatitude ()+","+mCurrentLocation.getLongitude ();
        FourSquareDataParser mJsonParser=new FourSquareDataParser (this,mURL,mListner);
        mJsonParser.loadJson ();
    }
    public void AddMarkers()
    {
        if(mNearByLocation!=null)
          mNearByLocation.clear ();
        for(int i=0;i<mLocationData.size ();i++) {
            LocationModel mItem = mLocationData.get (i);
            Log.d (TAG, "LL" + mItem.getLongitude () + mItem.getLatitude ());
            MarkerOptions marker = new MarkerOptions ().position (new LatLng (mItem.getLatitude (),mItem.getLongitude ()));
            marker.icon (BitmapDescriptorFactory.defaultMarker (BitmapDescriptorFactory.HUE_GREEN));
            marker.title (mItem.getName ());
            marker.snippet (mItem.getAddress ());
            //CameraPosition cameraPosition = new CameraPosition.Builder().target (new LatLng (mItem.getLatitude (), mItem.getLongitude ()).zoom (14).build ();
            googleMap.addMarker (marker);
            mNearByLocation.add (new LatLng (mItem.getLatitude (), mItem.getLongitude ()));


        }


    }


    private FourSquareDataParser.onJsonParseCompleted mListner= new FourSquareDataParser.onJsonParseCompleted(){

        @Override
        public void onParseSuccess(List<LocationModel> feed) {
            Log.d(TAG, "onParsesuccess size" + feed.size());
            if(mLocationData!=null) {
                mLocationData.clear ();

            }
            mLocationData=feed;
            AddMarkers();

        }

        @Override
        public void onParseFailure() {

            //Display Error Toast
            //     Log.d(TAG, "onParseFailure");
            Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_SHORT).show();
        }
    };
}


