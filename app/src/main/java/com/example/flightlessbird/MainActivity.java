package com.example.flightlessbird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Button signOutBtn;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signOutBtn = findViewById(R.id.signOutBtn);
        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize firebase user
        firebaseUser = firebaseAuth.getCurrentUser();


    }

    public void signOutBtn(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}