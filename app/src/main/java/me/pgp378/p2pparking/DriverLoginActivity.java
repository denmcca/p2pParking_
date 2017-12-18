package me.pgp378.p2pparking;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/*
/Login system for user acting as driver.
 */
public class DriverLoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin, mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private Boolean loginAttempt = false;

    /*
    /initializes when activity is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth = FirebaseAuth.getInstance();

        /*
        /Initializes Authorization listener for firebase
         */
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                } else if (loginAttempt) {
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);
        mRegistration = (Button) findViewById(R.id.registration);

        /*
        /When register button is tapped.
        /Takes user inputs and attempts to add to the firebase database.
        /If add is successful, logs user in and opens map activity.
         */
        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_email = "";
                String temp_password = "";

                temp_email = mEmail.getText().toString();
                temp_password = mPassword.getText().toString();

                loginAttempt = true;

                if (temp_email.matches("") || temp_password.length() < 6) {
                    if (temp_password.length() < 6) {
                        Toast.makeText(DriverLoginActivity.this, "password too short\nmust be at least 6 digits", Toast.LENGTH_SHORT).show();

                    }
                    if (temp_email.matches("")) {
                        Toast.makeText(DriverLoginActivity.this, "invalid email entered", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(DriverLoginActivity.this, "try again", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DriverLoginActivity.this, DriverLoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                final String email = temp_email;
                final String password = temp_password;

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(DriverLoginActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                        } else {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                            current_user_db.setValue(true);
                        }
                    }
                });
            }
        });

        /*
        /Activates when login button is tapped.
        /Takes user inputs and checks credentials against the firebase database.
        /If matching credentials are found, user is logged in to map activity.
         */
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp_email = "";
                String temp_password = "";

                temp_email = mEmail.getText().toString();
                temp_password = mPassword.getText().toString();

                loginAttempt = true;

                if (temp_email.matches("") || temp_password.length() < 1) {
                    Toast.makeText(DriverLoginActivity.this, "invalid email entered", Toast.LENGTH_SHORT).show();
                    Toast.makeText(DriverLoginActivity.this, "try again", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DriverLoginActivity.this, DriverLoginActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                final String email = temp_email;
                final String password = temp_password;

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(DriverLoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /*
    /Turns on firebase listener
     */
    @Override
    protected  void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    /*
    /Turns off firebase listener
     */
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    /*
    /Back button functionality.
     */
    private long backPressedTime;
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}