package com.sample.tourguide;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;

/**
 * Created by keyur on 06-08-2015.
 */
public class Define {
    //public static String GOOGLE_KEY="AIzaSyAY-pIjIay2nShWwyCF0QkjwY92d_3tvss";
    public static String CLIENT_ID="XWLGRC03I0EFFEFCSIKSRTDVN5IK5Y1JMBR4DOWOAK2TC25C";
    public static String CLINED_SECRET="K1QFNQVG0JSK22B0ASDZXRTDT2FEVBTTQS2KSESFRWVW1153";
    public  static boolean isDataConnAvailable(final Context mContext)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnected())
        {
            //No Internet
            return false;
        }
        return true;
    }
    public  static boolean isGPSEnable(final Context mContext)
    {
        LocationManager service = (LocationManager) mContext.getSystemService (mContext.LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public static enum ErrorStatus {
        No_GPS, No_Internet, Parse_Error
    }
    public static String FOUR_SQUARE_URL="https://api.foursquare.com/v2/venues/search?client_id="+Define.CLIENT_ID+"&client_secret="+Define.CLINED_SECRET+"&v=20150711&limit=100"+"&query=tourist%20attraction";
}
