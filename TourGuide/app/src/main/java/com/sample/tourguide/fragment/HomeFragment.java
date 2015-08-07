package com.sample.tourguide.fragment;

/**
 * Created by keyur on 07-08-2015.
 */
import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;


public class HomeFragment extends SupportMapFragment implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener {
    private static final String SUPPORT_MAP_BUNDLE_KEY = "MapOptions";
    private OnGoogleMapFragmentListener mCallback;
    protected static final String TAG = "basic-location-sample";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected Boolean mRequestingLocationUpdates=false;
    protected LocationRequest mLocationRequest;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    GoogleMap mCurrentMap;
    public HomeFragment() {
        // Required empty public constructor
        super ();
    }



    public static HomeFragment newInstance(GoogleMapOptions options) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(SUPPORT_MAP_BUNDLE_KEY, options);

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments (arguments);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
       // updateValuesFromBundle(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        // Inflate the layout for this fragment
       // return rootView;
        //return inflater.inflate(R.layout.fragment_home, container, false);
        View v = super.onCreateView (inflater, container, savedInstanceState);
       // Fragment fragment = getParentFragment();

        return v;
    }
    @Override
    public void onStart() {
        super.onStart ();
        mGoogleApiClient.connect ();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach (activity);

        buildGoogleApiClient ();
        try {
            mCallback = (OnGoogleMapFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getName() + " must implement OnGoogleMapFragmentListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach ();
    }
    @Override
    public void onResume() {
        super.onResume ();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates ();
        }
    }
    @Override
    public void onPause() {
        super.onPause ();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates ();
        }
    }
    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }


        super.onStop();
    }
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public static interface OnGoogleMapFragmentListener {
        void onMapReady (GoogleMap map);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i (TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode ());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i (TAG, "Connection suspended");
        mGoogleApiClient.connect ();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity ())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build ();
        createLocationRequest ();
       // startLocationUpdates ();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation (mGoogleApiClient);
        if (mLastLocation != null) {

            if (mCallback != null) {

                mCallback.onMapReady (setUpMap());
            }
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    private GoogleMap setUpMap() {
        if(mCurrentMap==null)
         mCurrentMap=getMap ();
        LatLng mLatLng=new LatLng(mLastLocation.getLatitude (),mLastLocation.getLongitude ()) ;

        // Enable MyLocation Layer of Google Map
        mCurrentMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE


        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();



        // Get latitude of the current location


        // Create a LatLng object for the current location


        // Show the current location in Google Map
        mCurrentMap.moveCamera (CameraUpdateFactory.newLatLng (mLatLng));

        // Zoom in the Google Map
        mCurrentMap.moveCamera (CameraUpdateFactory.newLatLngZoom (mLatLng, 15));
        mCurrentMap.animateCamera (CameraUpdateFactory.zoomTo (14), 2000, null);
        mCurrentMap.addMarker(new MarkerOptions().position(mLatLng).title("You are here!"));
       // mCurrentMap=getMap ();
        return mCurrentMap;
    }
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mCallback.onMapReady (setUpMap ());

    }
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

}