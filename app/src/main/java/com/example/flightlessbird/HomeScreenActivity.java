package com.example.flightlessbird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity implements RecyclerViewInterface {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {

        //get current user
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(HomeScreenActivity.this, LoginSignupActivity.class));
        }

        String userUID = user.getUid().toString();
        db = FirebaseFirestore.getInstance();

        //get user's trip data
        DocumentReference userDataRefDoc = db.collection("users").document(userUID);
        userDataRefDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String TAG = "firebase";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        try {
                            displayTrips(document);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void displayTrips(DocumentSnapshot documentSnapshot) throws JSONException {

        //get user data object
        userData = new UserData(documentSnapshot);

        //fill recycler view with icons
        RecyclerView recyclerView = findViewById(R.id.myTripsRecyclerView);

        //populate recycler view
        UT_RecyclerViewAdapter adapter = new UT_RecyclerViewAdapter(this, userData.userTrips, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    ////////////////////////////////////////////////////////////////
    // buttons
    ////////////////////////////////////////////////////////////////

    //click on trip
    @Override
    public void onItemClick(int position) {

        //create intent
        Intent intent = new Intent(this, TripDetailsActivity.class);

        //tell the intent which trip was clicked on
        intent.putExtra("TRIP", userData.userTrips.get(position));

        //start details activity
        startActivity(intent);
    }

    //click on logout
    public void logout(View view) {

        //sign out user
        FirebaseAuth.getInstance().signOut();

        //go to homescreen
        Intent intent = new Intent(this, LoginSignupActivity.class);
        startActivity(intent);

        //clear activity data so it is not seen if a user with no data logs in
        finish();
    }

    //click on new trip
    public void newTrip(View view) {

        //go to new trip activity
        Intent intent = new Intent(this, NewTripActivity.class);
        startActivity(intent);
    }
}