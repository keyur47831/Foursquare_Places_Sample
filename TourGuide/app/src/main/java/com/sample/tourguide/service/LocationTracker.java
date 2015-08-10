package com.sample.tourguide.service;

/**
 * Created by keyur on 08-08-2015.
 */






        import android.app.Service;
        import android.content.Context;

        import android.content.Intent;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.IBinder;

        import android.util.Log;
        import android.widget.Toast;

        import com.sample.tourguide.Define;
        import com.sample.tourguide.R;
        import com.sample.tourguide.activity.AppController;

/**
 * Created by keyur on 14-07-2015.
 * This is a Service running in background
 * to fetch the current mLocation of user
 * It implements Location Listener
 */
public class LocationTracker extends Service implements LocationListener {
    //context for GPS
    private final Context mContext;
    //Call-back to notify the change in mLocation
    protected onPositionChanged mLocationListner;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag to indicate is GPS access available
    boolean canGetLocation = false;

    Location mLocation; // Location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters


    // Declaring a Location Manager
    protected LocationManager locationManager;
    /*
    * This our default constructor to start mLocation data.
    * @param void
    * @return LocationResults
    */
    public LocationTracker(onPositionChanged listner)
    {
        this.mContext= AppController.getInstance ();
        this.mLocationListner =listner;
        //init the mLocation service
        getmLocation ();
    }
    /*
    * This function will init all mLocation related data
    * and start listening for changes in GPS mLocation.
    * @param void
    * @return Location
    */
    public Location getmLocation ()
    {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get mLocation from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            Define.MIN_TIME_BW_UPDATES,
                            Define.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        mLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            latitude = mLocation.getLatitude();
                            longitude = mLocation.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (mLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                Define.MIN_TIME_BW_UPDATES,
                                Define.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            mLocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                latitude = mLocation.getLatitude();
                                longitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLocation;
    }
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(mLocation != null){
            latitude = mLocation.getLatitude ();
        }

        return latitude;
    }

    /*
     * Function to get longitude
     */
    public double getLongitude(){
        if(mLocation != null){
            longitude = mLocation.getLongitude();
        }

        return longitude;
    }

    /*
     * Function to check GPS/wifi enabled
     *
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /*
     * Function to notify change in mLocation
     * we will give call-back to Presenter class
     * which in turn will update the data
     */
    @Override
    public void onLocationChanged(Location location) {
        //notify change in mLocation to presenter class
        mLocationListner.getNewLocation (location);
    }
    //override method which we do not implement
    //as per our requirement not all functionality used
    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    public interface onPositionChanged {
        void getNewLocation(Location location);
    }
    /*
     * This function is used for Unit Testing only.

     */
    public static void LocationTest()
    {

        final LocationTracker testTracker=new LocationTracker(new onPositionChanged(){
            public void getNewLocation(Location location)
            {

                if(location!=null)
                {
                    Log.d("LocationTest", "canGetLocationSuccess");

                }
                else
                {

                    Log.d("LocationTest","canGetLocationfailure");


                }

            }


        });
        if(testTracker.canGetLocation()) {
            Log.d ("LocationTest", "canGetLocationSuccess");
        }
        else
        {
            Log.d("LocationTest","canGetLocationfailure");
        }


    }
}
