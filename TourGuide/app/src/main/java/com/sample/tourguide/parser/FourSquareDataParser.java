package com.sample.tourguide.parser;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sample.tourguide.R;
import com.sample.tourguide.activity.AppController;
import com.sample.tourguide.model.LocationModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by keyur on 08-08-2015.
 */
public class FourSquareDataParser {
    protected onJsonParseCompleted mJsonParserListner;
    //reference of context
    Context mContext;
    //foursquare URL to fetch data
    String mURL;
    // for logs
    String TAG=FourSquareDataParser.class.getSimpleName ();
    List<LocationModel> ParserData = new ArrayList<> ();

    private RequestQueue mRequestQueue;
    public FourSquareDataParser(String url, onJsonParseCompleted listner)
    {
        super();

        this.mURL =url;
        //this listner will perform call-back to our UI Activity
        this.mJsonParserListner =listner;
        //Init our Volley request queue
        mRequestQueue=AppController.getInstance ().getRequestQueue ();
    }
    public void loadJson()
    {

        //Create Instance of JsonObjectRequest
        // This object will make asynchronous http call to fetch data
        // It will register callback function onResponse for the data
        // and will also provide callback function for error handling
                //JsonObject request using volley
                JsonObjectRequest request = new JsonObjectRequest(mURL, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                parseJSONFromString (response.toString ());

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
                );
                //Create Instance of Volley Request
                //Volley class has singleton implementation so only 1 object
                //will be used throughout the life cycle of application.
                //All the requests should be added in the request queue
                request.setTag(TAG);
                mRequestQueue.add (request);

            }
       // }


    /*
     * This function will parse the JSON data into the logical Class data.
     */
    private void parseJSONFromString(final String JSONdata) {


        try {
            JSONObject jsonObject = new JSONObject(JSONdata);
            //check if we got correct response
            if (jsonObject.has("response"))
                //check if venues tag is present
                if (jsonObject.getJSONObject ("response").has("venues")) {
                    //retrive the array of venues
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");
                    //we need to loop through each venues node to parse data
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //retrive each venue details
                        JSONObject jsonVenues = (JSONObject) jsonArray.get(i);
                        LocationModel SingleLocationRow = new LocationModel();
                        //check if name of cafe available
                        if (jsonArray.getJSONObject(i).has("name")) {
                            SingleLocationRow.setName (jsonArray.getJSONObject (i).getString ("name"));
                            //   Log.d(TAG, SingleLocationRow.getShopName());
                        }
                        //get the location array to parse address
                        //we can also query formattedaddress
                        //but from the data we can see it is not mandatory
                        //filed and is received empty also
                        JSONObject jlocation = jsonVenues.getJSONObject("location");
                        //error handling to ensure we do not
                        //get runtime error
                        if (jlocation!=null) {
                            //local stringbuilder as we will
                            //keep adding the address details
                            StringBuilder strAddres=new StringBuilder();
                            if (jlocation.has("address")) {
                                strAddres.append(jlocation.get("address"));
                                strAddres.append(" ");
                            }

                            if (jlocation.has("crossStreet")) {
                                strAddres.append(jlocation.get("crossStreet"));
                                strAddres.append(" ");

                            }
                            if (jlocation.has("city")) {
                                strAddres.append(jlocation.get("city"));
                                strAddres.append(" ");

                            }
                            if (jlocation.has("state")) {
                                strAddres.append(jlocation.get("state"));
                                strAddres.append(" ");

                            }
                            if (jlocation.has("country")) {
                                strAddres.append(jlocation.get("country"));
                                strAddres.append(" ");

                            }

                            if (jlocation.has("postalCode")) {
                                strAddres.append(jlocation.get("postalCode"));

                            }
                            //set the address of venue
                            SingleLocationRow.setAddress(strAddres.toString());
                            // Log.d(TAG, SingleLocationRow.getAddress());
                            //retrive latitude and longitude details
                            if (jlocation.has("lat")) {
                                SingleLocationRow.setLatitude(jlocation.getDouble("lat"));
                                //   Log.d(TAG, String.valueOf(SingleLocationRow.getLatitude()));
                            }
                            if (jlocation.has("lng")) {
                                SingleLocationRow.setLongitude(jlocation.getDouble("lng"));
                                //  Log.d(TAG, String.valueOf(SingleLocationRow.getLongitude()));

                            }
                            //retrive the distance information
                            if (jlocation.has("distance")) {
                                SingleLocationRow.setDistance(jlocation.getInt("distance"));
                                //    Log.d(TAG, String.valueOf(SingleLocationRow.getDistance()));

                            }
                        }

                        //add each single object to arraylist
                        ParserData.add(SingleLocationRow);
                    }

                }


        } catch (Exception e) {

            e.printStackTrace();
            //Notify error to UI
            mJsonParserListner.onParseFailure ();
        }
        //check if we received any data
        if(ParserData == null) {
            //notify failure
            mJsonParserListner.onParseFailure ();

        }
        else {
            //as per the requirements, we need to sort
            //the location based on distance
            //we have defined our custom compartor to
            //support sorting
            Collections.sort (ParserData, LocationModel.CompareDistance);
            //notify success with sorted arraylist
            mJsonParserListner.onParseSuccess (ParserData);

        }

    }
    /*
     * This interface provides a call-back mechanism for Activity class.
     * It has two callback functions
     * 1) onParseSuccess returns Arraylist with parsed data
     * 2) onParseFailure indicates error in parsing the data.
     */
    public interface onJsonParseCompleted {

        void onParseSuccess(List<LocationModel> Data);
        void onParseFailure();

    }
    public static void JSONFeedParserTest( String url) {
        final FourSquareDataParser parser = new FourSquareDataParser(
                url, new onJsonParseCompleted() {

            @Override
            public void onParseSuccess(List<LocationModel>  feed) {
                Log.d ("JsonParseData", "JSONParseSuccess " +
                        feed.size () + " records.");

            }

            @Override
            public void onParseFailure() {
                Log.d ("JsonParseData", "JSONParsefailure");

            }

        });
        parser.loadJson();
    }

}
