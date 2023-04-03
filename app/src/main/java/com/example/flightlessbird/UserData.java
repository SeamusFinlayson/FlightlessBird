package com.example.flightlessbird;

import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.jar.JarException;

public class UserData {

    ArrayList<UserTrip> userTrips;

    public UserData(DocumentSnapshot userDataDocumentSnapshot) throws JSONException {

        //get user data JSON object from downloaded document
        JSONObject userDataJSON = new JSONObject(userDataDocumentSnapshot.getData());

        //get array of user trips in JSON format
        JSONArray userTripsJSON = userDataJSON.getJSONArray("trips");

        //read user trips from JSON array into arrayList
        this.userTrips = new ArrayList<UserTrip>();
        UserTrip userTrip;
        for (Integer i = 0; i < userTripsJSON.length(); i++) {

            userTrip = new UserTrip(userTripsJSON.getJSONObject(i));
            this.userTrips.add(userTrip);
        }
    }
}

