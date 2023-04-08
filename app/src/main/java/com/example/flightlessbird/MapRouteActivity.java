package com.example.flightlessbird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.*;
import java.io.*;

public class MapRouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap myMap;

    String originStationName;
    String destinationStationName;
    Boolean originStation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_route);

        TextView originTextView = findViewById(R.id.originStationTextView);
        TextView destinationTextview = findViewById(R.id.destinationStationTextView);
        Intent intent = getIntent();
        originStationName = intent.getStringExtra("Origin");
        destinationStationName = intent.getStringExtra("Destination");
        originTextView.setText("" + originStationName + " Station");
        destinationTextview.setText("" + destinationStationName + " Station");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        LatLng uk = new LatLng(55.01290202072023, -2.649627483877282);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uk, 5));

        drawStation(originStationName, true);
        drawStation(destinationStationName,false);
    }

    public void drawStation(String stationName, Boolean originStation) {

        this.originStation = originStation;
        String url = getLocationsUrl(stationName);

        //get map marker
        new DownloadTask().execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            Log.d("data is ", data);
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (!result.equals("")) {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equals("OK")) {
                        JSONArray jsonResults = jsonObject.getJSONArray("results");
                        JSONObject jsonDataAt0 = jsonResults.getJSONObject(0);
                        JSONObject geometry = jsonDataAt0.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Float latitude = Float.parseFloat(location.getString("lat"));
                        Float longitude = Float.parseFloat(location.getString("lng"));
                        LatLng latLng = new LatLng(latitude, longitude);

                        myMap.addMarker(new MarkerOptions().position(latLng).title(jsonDataAt0.getString("name"))).showInfoWindow();
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getLocationsUrl(String placeName) {

        // search region is uk
        //put input place name in good format
        placeName = placeName.replaceAll(" ", "+") + "+train+station+in+united+kingdom";

        String nameString = "query=" + placeName;
        String parameters = nameString + "&key=" + "AIzaSyDxmoBmIey9DH4ELri6msK9Hz1thAzckCQ";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/" + output + "?" + parameters;
        //String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=52.3065,-1.9458&radius=1000&name=Redditch+Station&key=AIzaSyDxmoBmIey9DH4ELri6msK9Hz1thAzckCQ";

        Log.d("URL is", url);
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

}