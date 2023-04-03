package com.example.flightlessbird;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText etEmail;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        etEmail = findViewById(R.id.editTextEmailAddress);
        etPassword = findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    public void logIn(View view) {

        loginUser();
    }

    private void loginUser() {

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            etEmail.setError("Email Required");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password Required");
            etPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(LoginSignupActivity.this, HomeScreenActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginSignupActivity.this, "Account Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void signUp(View view) {

        createUser();
    }

    private void  createUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(email)) {
            etEmail.setError("Email Required");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password Required");
            etPassword.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(LoginSignupActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                startActivity(new Intent(LoginSignupActivity.this, HomeScreenActivity.class));
                            } else {

                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginSignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
        System.exit(0);
    }
}