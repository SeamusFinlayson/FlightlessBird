package com.example.flightlessbird;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;


import org.json.JSONException;

import java.util.ArrayList;

public class TripDetailsActivity extends AppCompatActivity implements RecyclerViewInterface {

    private static Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    UserTrip userTrip;

    boolean deleteAttempted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        Intent intent = getIntent();
        userTrip = intent.getParcelableExtra("TRIP");

        //limit header text width
        TextView header = findViewById(R.id.detailsHeaderTextView);
        if (userTrip.getTripName().length() > 40) {
            String truncatedName = userTrip.getTripName().substring(0,28) + "...";
            header.setText(truncatedName);
        } else {
            header.setText(userTrip.getTripName());

        }

        RecyclerView recyclerView = findViewById(R.id.detailsRecyclerView);

        TR_RecyclerViewAdapter adapter = new TR_RecyclerViewAdapter(this, userTrip.getTripRoutes(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        deleteAttempted = false;
    }

    public void deleteTrip (View view) {
        if (deleteAttempted == false) {
            deleteAttempted = true;
            mAuth = FirebaseAuth.getInstance();

            FirebaseUser user = mAuth.getCurrentUser();
            String userUID = user.getUid().toString();
            db = FirebaseFirestore.getInstance();

            //get user's trip data
            DocumentReference userData = db.collection("users").document(userUID);
            userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    String TAG = "firebase";
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            try {
                                UserData userData = new UserData(document);

                                int indexToDelete = -1;
                                String targetName = userTrip.getTripName();;
                                String candidateName;
                                for (int i = 0; i < userData.userTrips.size(); i++) {

                                    candidateName = userData.userTrips.get(i).getTripName();
                                    if (targetName.equals(candidateName)) {
                                        indexToDelete = i;
                                        break;
                                    }
                                }

                                DocumentReference docRef = db.collection("users").document(userUID);
                                String fieldToDelete = "0";

                                // Use a transaction to update the document and remove the first element of the trips array
                                int finalIndexToDelete = indexToDelete;
                                db.runTransaction(new Transaction.Function<Void>() {
                                            @Override
                                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                                DocumentSnapshot snapshot = transaction.get(docRef);
                                                ArrayList<String> trips = (ArrayList<String>) snapshot.get("trips");
                                                if (trips != null && trips.size() > 0) {
                                                    trips.remove(finalIndexToDelete);
                                                    transaction.update(docRef, "trips", trips);
                                                }
                                                return null;
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Document successfully updated
                                                Toast.makeText(TripDetailsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(TripDetailsActivity.this, HomeScreenActivity.class));
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Error updating document
                                                Toast.makeText(TripDetailsActivity.this, "Could Not Delete", Toast.LENGTH_SHORT).show();
                                                deleteAttempted = false;
                                            }
                                        });
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
        } else {
            Toast.makeText(TripDetailsActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(TripDetailsActivity.this, MapRouteActivity.class);
        intent.putExtra("Origin", userTrip.getTripRoutes().get(position).getOriginStationName());
        intent.putExtra("Destination", userTrip.getTripRoutes().get(position).getDestinationStationName());
        startActivity(intent);
    }
}