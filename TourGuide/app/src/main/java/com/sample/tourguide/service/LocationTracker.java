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

        import com.tourguide.R;

//import com.foursquare.sample.coffeesearch.R;

/**
 * Created by keyur on 14-07-2015.
 * This is a Service running in background
 * to fetch the current location of user
 * It implements Location Listener
 */
public class LocationTracker extends Service implements LocationListener {
    //context for GPS
    private final Context mContext;
    //Call-back to notify the change in location
    protected onPositionChanged LocationListner;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag to indicate is GPS access available
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 20 * 1; // 20 sec aggressive

    // Declaring a Location Manager
    protected LocationManager locationManager;
    /*
    * This our default constructor to start location data.
    * @param void
    * @return LocationResults
    */
    public LocationTracker(Context context ,onPositionChanged listner)
    {
        this.mContext=context;
        this.LocationListner=listner;
        //init the location service
        getLocation();
    }
    /*
    * This function will init all location related data
    * and start listening for changes in GPS location.
    * @param void
    * @return Location
    */
    public Location getLocation()
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
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    //    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        //      Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
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
        if(location != null){
            latitude = location.getLatitude ();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will launch Settings Options
     * */

    /*
     * Function to notify change in location
     * we will give call-back to Presenter class
     * which in turn will update the data
     */
    @Override
    public void onLocationChanged(Location location) {
        //notify change in location to presenter class
        LocationListner.getNewLocation(location);
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
     * Using Robotium or any other such tools, we can verify if
     * GPS tracking is working correctly or not
     * Refer test case no. 6
     * It has 1 callback functions
     * 1) getNewLocation returns the GPS current location
     * from our GPS service running in background
     */
    public static void LocationTest(final Context context)
    {

        final LocationTracker testTracker=new LocationTracker(context,new onPositionChanged(){
            public void getNewLocation(Location location)
            {

                if(location!=null)
                {
                    Log.d("LocationTest", "canGetLocationSuccess");
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Log.d("LocationTest","canGetLocationfailure");
                    Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show();

                }

            }


        });
        if(testTracker.canGetLocation()) {
            //  Location TestLocation = testTracker.getLocation();
            Log.d ("LocationTest", "canGetLocationSuccess");


        }
        else
        {

            Log.d("LocationTest","canGetLocationfailure");

        }


    }
}
