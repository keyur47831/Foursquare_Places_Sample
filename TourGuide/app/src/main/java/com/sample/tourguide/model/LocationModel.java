package com.sample.tourguide.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by keyur on 08-08-2015.
 */
public class LocationModel {

    private String Name;
    private int Distance;
    private String Address;
    private double Latitude;
    private double Longitude;
    /*
     * This our default constructor to init data object.
     * @param void
     * @return LocationResults
     */

    public LocationModel()
    {
        super();

        this.Name="";
        this.Address="";
        this.Latitude=0;
        this.Longitude=0;
        this.Distance=0;


    }
    /*
     * Below are our get and set properties.
     * currently created them as public so that we can
     * create/modify objects on the fly
     */
    public int getDistance()
    {
        return this.Distance;
    }
    public void setDistance(int value)
    {
        this.Distance=value;
    }
    public String getAddress()
    {
        return this.Address;
    }
    public void setAddress(String value)
    {
        this.Address=value;
    }
    public double getLatitude()
    {
        return  this.Latitude;
    }
    public void setLatitude(double value)
    {
        this.Latitude=value;
    }
    public double getLongitude()
    {
        return  this.Longitude;
    }
    public void setLongitude(double value)
    {
        this.Longitude=value;
    }

    public String getName()
    {
        return this.Name;
    }
    public void setName(String value)
    {
        this.Name=value;
    }
    /*
      * We create a Comparator function to compare our class object.
      * currently we are sorting only by distance but going ahead
      * if sorting is required by other properties, we can define here
      * @param LocationResults
      * @param LocationResults
      * @return int
      */

    public static Comparator<LocationModel> CompareDistance = new Comparator<LocationModel> () {

        public int compare(LocationModel s1, LocationModel s2) {

            int Distance1 = s1.getDistance();
            int Distance2 = s2.getDistance();
	        /*For ascending order*/
            return Distance1-Distance2;


            /*For descending order*/
            // return Distance2-Distance1;
        }};
    public static Comparator<LatLng> createComparator(LatLng p)
    {
        final LatLng finalP = new LatLng (p.latitude,p.longitude);
        return new Comparator<LatLng>()
        {
            @Override
            public int compare(LatLng p0, LatLng p1)
            {
               // float[] result1 = new float[1];
                //float[] result2 = new float[1];
              //  android.location.Location.distanceBetween(finalP.latitude, finalP.longitude, p0.latitude, p0.longitude, result1);
                //android.location.Location.distanceBetween(finalP.latitude, finalP.longitude, p1.latitude, p1.longitude, result2);
               double square1=distanceSq (finalP.latitude, finalP.longitude,p0.latitude,p0.longitude);
                double square2=distanceSq (finalP.latitude, finalP.longitude,p1.latitude,p1.longitude);

                return Double.compare(square1, square2);
            }

        };
    }
    public static double distanceSq(double x1, double y1, double x2, double y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }





}
