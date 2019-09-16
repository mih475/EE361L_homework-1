package com.example.homework_1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.example.homework_1.ui.main.MainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;




public class MainActivity extends AppCompatActivity {

     String TAG = MainActivity.class.getSimpleName();


    private HashMap<String, Double> lat_lng;

     String formattedQuery= new String();




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




    }

    /**
     * Show a toast
     * @param view -- the view that is clicked
     */
    public void toastMe(View view){
        // Toast myToast = Toast.makeText(this, message, duration);
        Toast myToast = Toast.makeText(this, "Json Data is \n" +
                        "            downloading",
                Toast.LENGTH_SHORT);
        myToast.show();

        SearchView searchView = (SearchView) findViewById(R.id.InputLocation);

        String query = searchView.getQuery().toString();

        for(String word : query.split(" ") ){
            formattedQuery = formattedQuery + "+" + word;
        }

        Log.e(TAG, "formatted text output " + formattedQuery);

        new GetCoordinates().execute();


    }

    private class GetCoordinates extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {


            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + formattedQuery + "&key=AIzaSyAxU0GQ13rtrBx7Y6_CnjSByzX3AE0hvfQ";
            Log.e(TAG, "url search " + url);
            String jsonStr = sh.makeServiceCall(url);



            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray results = jsonObj.getJSONArray("results");

                    //Getting the geometry
                    JSONObject geometry =  results.getJSONObject(0).getJSONObject("geometry");


                    //Getting the Location which is under geometry
                    JSONObject location = geometry.getJSONObject("location");

                    Double latitude = location.getDouble("lat");
                    Double longitude = location.getDouble("lng");

                    lat_lng.put("lat", latitude);
                    lat_lng.put("lng", longitude);

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
            TextView showTemp = (TextView) findViewById(R.id.tempVal);
            TextView showSpeed = (TextView) findViewById(R.id.speedVal);
            showTemp.setText(Double.toString(lat_lng.get("lat")));
            showSpeed.setText(Double.toString(lat_lng.get("lng")));

        }
    }





}
