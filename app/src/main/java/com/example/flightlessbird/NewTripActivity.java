package com.example.flightlessbird;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import java.util.Map;
import java.util.Random;

public class NewTripActivity extends AppCompatActivity {

    private Spinner cityCountSpinner;
    private Spinner tripDurationSpinner;
    private EditText tripNameEditText;
    private Button createTripButton;
    private ArrayList<TripRoute> tripRoutes = new ArrayList<>();
    private UserTrip userTrip;
    private int numDays;
    int numCities;
    int daysInEach;
    private EditText dateInputEditText;
    LocalDate startDate;
    private String startCity;
    private static final String APP_ID = "cd8b9027";
    private static final String API_KEY = "3bb439c540051cd8dad2e1fa4e542d71";
    private boolean canceled;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    Boolean createButtonPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        tripNameEditText = findViewById(R.id.tripNameEditText);
        createTripButton = findViewById(R.id.createTripButton);

        tripDurationSpinner = findViewById(R.id.tripDurationSpinner);
        Integer[] items = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item, items);
        tripDurationSpinner.setAdapter(adapter);

        cityCountSpinner = findViewById(R.id.cityCountSPinner);
        Integer[] items2 = new Integer[]{1,2,3};
        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item, items2);
        cityCountSpinner.setAdapter(adapter2);

        Spinner citySpinner = findViewById(R.id.startingCitySPinner);

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        dateInputEditText = findViewById(R.id.date_input_edit_text);

        firebaseAuth = FirebaseAuth.getInstance();

        currentUser = firebaseAuth.getCurrentUser();
        createButtonPressed = false;

        dateInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(NewTripActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                        dateInputEditText.setText(selectedDate.toString());
                        startDate = selectedDate;
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createButtonPressed == false) {
                    if (tripNameEditText.getText().toString().isEmpty()){
                        Toast.makeText(NewTripActivity.this, "Please enter a trip name", Toast.LENGTH_SHORT).show();
                    } else if (cityCountSpinner.getSelectedItem().toString().isEmpty()) {
                        Toast.makeText(NewTripActivity.this, "Please enter the number of cities.", Toast.LENGTH_SHORT).show();
                    } else if (tripDurationSpinner.getSelectedItem().toString().isEmpty()) {
                        Toast.makeText(NewTripActivity.this, "Please enter the desired trip duration.", Toast.LENGTH_SHORT).show();
                    } else if (dateInputEditText.getText().toString().isEmpty()) {
                        Toast.makeText(NewTripActivity.this, "Please enter the desired departure date.", Toast.LENGTH_SHORT).show();
                    } else if (citySpinner.getSelectedItem().toString().isEmpty()) {
                        Toast.makeText(NewTripActivity.this, "Please enter the starting city.", Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(tripDurationSpinner.getSelectedItem().toString()) < Integer.parseInt(cityCountSpinner.getSelectedItem().toString())) {
                        Toast.makeText(NewTripActivity.this, "Trip duration must be greater than or equal to Number of Cities.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewTripActivity.this, "Creating Vacation", Toast.LENGTH_SHORT).show();
                        createButtonPressed = true;
                        numDays = Integer.parseInt(tripDurationSpinner.getSelectedItem().toString());
                        numCities = Integer.parseInt(cityCountSpinner.getSelectedItem().toString());
                        daysInEach = (int) Math.floor((numDays-numCities)/numCities);
                        startCity = citySpinner.getSelectedItem().toString();
                        new GetTransitRoutesTask().execute(startCity);
                    }
                } else {
                    Toast.makeText(NewTripActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class GetTransitRoutesTask extends AsyncTask<String, Void, ArrayList<TripRoute>> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<TripRoute> doInBackground(String... params) {
            canceled = false;
            String location = params[0]; // Get the location input
            ArrayList<String> transitRoutes = new ArrayList<>(); // Initialize the transit routes list

            try {
                String stationCode = getStationCode(location);
                // If a station code is found, fetch transit routes and destinations
                if (!stationCode.isEmpty()) {
                    tripRoutes.add(new TripRoute());
                    tripRoutes.get(0).setOriginStationCode(stationCode);
                    tripRoutes.get(0).setDepartureDate(startDate.toString());
                    LocalDate lastDate = startDate;

                    for (int i = 0; i < numCities; i++){
                        if (!canceled) {
                            // Build the URL for the Transport API request to get departure information
                            URL url = new URL("https://transportapi.com/v3/uk/train/station_timetables/" + tripRoutes.get(i).getOriginStationCode() + ".json?app_id=" + APP_ID + "&app_key=" + API_KEY + "&limit=5&station_detail=destination");//"&datetime=" + trip.get(i).getDepartureDate() + "T00:00:00+17:00:00");
                            Log.d("URL", url.toString());
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Open the connection
                            connection.setRequestMethod("GET"); // Set the request method to GET

                            // Read the response from the API
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;

                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            reader.close();

                            // Parse the JSON response
                            JSONObject jsonResponse = new JSONObject(response.toString());
                            JSONArray jsonArray = jsonResponse.getJSONObject("departures").getJSONArray("all");

                            Random rand = new Random();

                            if (jsonArray.length() <= 0){
                                Log.e("Cancel", "API returned no trips, trip generation should be cancelled.");
                                canceled = true;
                                break;
                            }
                            int rand_int = rand.nextInt(jsonArray.length());

                            //Pick random route index from jsonArray;
                            JSONObject jsonObject = jsonArray.getJSONObject(rand_int);

                            tripRoutes.get(i).setOriginStationName(jsonResponse.getString("station_name"));
                            tripRoutes.get(i).setDestinationStationCode(jsonObject.getJSONObject("station_detail").getJSONObject("destination").getString("station_code"));
                            tripRoutes.get(i).setDestinationStationName(jsonObject.getString("destination_name"));
                            tripRoutes.get(i).setDepartureTime(jsonObject.getString("aimed_departure_time"));
                            tripRoutes.get(i).setArrivalTime(jsonObject.getJSONObject("station_detail").getJSONObject("destination").getString("aimed_arrival_time"));
                            tripRoutes.get(i).setOperatorName(jsonObject.getString("operator_name"));

                            if (i != numCities-1){
                                tripRoutes.add(new TripRoute());
                                tripRoutes.get(i+1).setOriginStationCode(tripRoutes.get(i).getDestinationStationCode()); //will be set in previous loop
                                lastDate = lastDate.plusDays(daysInEach+1);
                                tripRoutes.get(i+1).setDepartureDate(lastDate.plusDays(daysInEach+1).toString()); //will be set in previous loop
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return tripRoutes;
        }

        // This function fetches the station code for a given location
        private String getStationCode(String location) {
            try {
                // Build the URL for the Transport API request to get the station code
                URL url = new URL("https://transportapi.com/v3/uk/places.json?query=" + location + "&type=train_station&app_id=" + APP_ID + "&app_key=" + API_KEY);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // Open the connection
                connection.setRequestMethod("GET"); // Set the request method to GET

                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray jsonArray = jsonResponse.getJSONArray("member");

                // If there are train stations, get the station code of the first one
                if (jsonArray.length() > 0) {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    return jsonObject.getString("station_code");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return ""; // Return an empty string if no station code is found or an error occurs
        }

        @Override
        public void onPostExecute(ArrayList<TripRoute> result) {
            if (canceled){
                tripRoutes.clear();
                Toast.makeText(NewTripActivity.this, "Couldn't make route. Please change settings and try again.", Toast.LENGTH_SHORT).show();
                createButtonPressed = false;
            } else {
                if(tripRoutes != null && result.size() != 0) {
                    uploadRoutesToFirestore(result);
                    tripRoutes.clear();
                }
                else {
                    Toast.makeText(NewTripActivity.this, "Check internet connection and try again.", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Routes list is null. Cannot upload to Firestore.");
                }
            }
        }
    }

    private void uploadRoutesToFirestore(ArrayList<TripRoute> tripRoutes) {
        if (tripRoutes == null || tripRoutes.size() == 0) {
            Log.e("Firestore", "Routes list is null. Cannot upload to Firestore.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());

        Map<String, Object> trip = new HashMap<>();
        trip.put("routes", tripRoutes);
        trip.put("name", tripNameEditText.getText().toString());
        trip.put("tripDuration", numDays);
        trip.put("numCities", numCities);

        //create user trip object String tripName, Integer stationCount, Integer tripDuration, ArrayList<TripRoute> tripRoutes
        userTrip = new UserTrip(tripNameEditText.getText().toString(), numCities, numDays, tripRoutes);

        userDocRef.set(Collections.singletonMap("trips", FieldValue.arrayUnion(trip)), SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Trip added successfully"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error adding trip", e));

        //create intent
        Intent intent = new Intent(NewTripActivity.this, TripDetailsActivity.class);

        //tell the intent which trip was clicked on
        intent.putExtra("TRIP", userTrip);
        startActivity(intent);
    }
}