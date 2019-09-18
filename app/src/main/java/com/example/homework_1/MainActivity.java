package com.example.homework_1;

import androidx.appcompat.app.AppCompatActivity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.homework_1.ui.main.MainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;




public class MainActivity extends AppCompatActivity {

    // Miguel started driving
    String TAG = MainActivity.class.getSimpleName();

    String url = new String();
    String weatherDataURL = new String();
    private HashMap<String, Double> lat_lng;
    String formattedQuery = new String();

    RequestQueue requestQueue;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
        lat_lng = new HashMap<>();

        // Instantiate the RequestQueue with the cache and network.
        requestQueue  = Volley.newRequestQueue(this);


    }

    /**
     * Show a toast
     *
     * @param view -- the view that is clicked
     */
    public void toastMe(View view) {
        // Toast myToast = Toast.makeText(this, message, duration);
        Toast myToast = Toast.makeText(this, "Json Data is \n" +
                        "            downloading",
                Toast.LENGTH_SHORT);
        myToast.show();

        SearchView searchView = (SearchView) findViewById(R.id.InputLocation);

        String query = searchView.getQuery().toString();

        if(query.length() != 0){
            for (String word : query.split(" ")) {
                formattedQuery = formattedQuery + "+" + word;
            }

            Log.e(TAG, "formatted text output " + formattedQuery);

            new GetCoordinates().execute();
        }
        else {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
        }

    }

    public void getInfo() {

        final TextView showTemp = (TextView) findViewById(R.id.tempVal);
        final TextView showSpeed = (TextView) findViewById(R.id.speedVal);
        final TextView showHumidity = (TextView) findViewById(R.id.humidVal);
        final TextView showPrecipitation = (TextView) findViewById(R.id.preciVal);


        if(lat_lng.get("lat") != null || lat_lng.size() != 0){
            // Miguel stooped and Muhammad started driving
            weatherDataURL = "https://api.darksky.net/forecast/b83b1a21964642d7c104391dc410beb9/" + lat_lng.get("lat").toString() + "," + lat_lng.get("lng").toString();
            Log.e(TAG, "weather URL:" + weatherDataURL);
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, weatherDataURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e(TAG, "url search " + response);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);

                                    JSONObject currently =  jsonObj.getJSONObject("currently");

//                                //Getting the weather data
//                                JSONObject temperature = currently.getJSONObject("temperature");
//                                JSONObject humidity = currently.getJSONObject("humidity");
//                                JSONObject windSpeed = currently.getJSONObject("windSpeed");
//                                JSONObject precipitation = currently.getJSONObject("precipProbability");



                                    //Get Doubles from JSON objects
                                    Double temperatureDouble = currently.getDouble("temperature");
                                    Double humidityDouble = currently.getDouble("humidity");
                                    Double windSpeedDouble = currently.getDouble("windSpeed");
                                    Double precipProbabilityDouble = currently.getDouble("precipProbability");

                                    //Put values in textViews
                                    showTemp.setText(temperatureDouble.toString());
                                    showSpeed.setText(windSpeedDouble.toString());
                                    showHumidity.setText(humidityDouble.toString());
                                    showPrecipitation.setText(precipProbabilityDouble.toString());

                                    // Muhammad stopped and Miguel started driving
                                } catch (final JSONException e) {
                                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Json parsing error: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }

                            } else {

                                Toast.makeText(getApplicationContext(),
                                        "No Input!",
                                        Toast.LENGTH_LONG).show();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Invalid or no weather provided!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "That didn't work ");
                }
            });

// Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        }
        else{
            Toast.makeText(getApplicationContext(),
                                    "Invalid or no weather provided!",
                                    Toast.LENGTH_LONG).show();
        }

    }

    private class GetCoordinates extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute()throws RuntimeException {
            try {
                super.onPreExecute();
            }
            catch (RuntimeException e){
                throw e;
            }
        }

        @Override
        protected Void doInBackground(Void... arg0)throws RuntimeException {

            // Miguel stopped and Muhammad started driving
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + formattedQuery + "&key=AIzaSyAxU0GQ13rtrBx7Y6_CnjSByzX3AE0hvfQ";
            formattedQuery = "";
            Log.e(TAG, "url search " + url);
            String jsonStr = sh.makeServiceCall(url);


            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {

                JSONArray results= new JSONArray();
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    results = jsonObj.getJSONArray("results");

                    if(results.length() == 0){

                        Thread thread = new Thread(){
                            public void run(){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        };
                        thread.start();

//                        Toast.makeText(getApplicationContext(),
//                                "Invalid or no weather provided!",
//                                Toast.LENGTH_LONG).show();
                    }
                    //Getting the geometry
                    JSONObject geometry = results.getJSONObject(0).getJSONObject("geometry");


                    //Getting the Location which is under geometry
                    JSONObject location = geometry.getJSONObject("location");

                    Double latitude = location.getDouble("lat");
                    Double longitude = location.getDouble("lng");

                    lat_lng.clear();
                    lat_lng.put("lat", latitude);
                    lat_lng.put("lng", longitude);

                // Muhammad stopped and Miguel started driving
                } catch (final JSONException e) {

                    if(results.length() != 0){
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            } else {

                Toast.makeText(getApplicationContext(),
                        "Invalid or no weather provided!",
                        Toast.LENGTH_LONG).show();

                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            getInfo();

        }

    // Miguel stopped
    }
}
