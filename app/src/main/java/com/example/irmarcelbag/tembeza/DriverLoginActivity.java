package com.example.irmarcelbag.tembeza;

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

public class DriverLoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin, mRegistration;
    //Get the Database Library from FireBqse
    private FirebaseAuth nAuth;
    //A listener of when the status change
    //private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    //private FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        //Calling the instance
        nAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Check the users status
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent= new Intent(DriverLoginActivity.this, DriverMapActivity.class);
                   startActivity(intent);
                   finish();
                   return;
                }
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login);
        mRegistration = findViewById(R.id.registration);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              final String email = mEmail.getText().toString();
              final String password = mPassword.getText().toString();
              nAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if(!task.isSuccessful()){
                          Toast.makeText(DriverLoginActivity.this, "Sign Up error", Toast.LENGTH_SHORT).show();
                      }else {
                          // Putting the ID of The User in FB Database in ==>Users ===Riders child
                          //First Getting the User ID
                          String user_id = nAuth.getCurrentUser().getUid();
                          DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_id);
                         //Setting the Database Reference so that it save to Database
                          current_user_db.setValue(true);
                      }
                  }
              });
            }
        });
        //Login Button if the Sign in is fail is going to call the next page
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(DriverLoginActivity.this, "Sign in error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    // Start the Listener when the Activity is call

    @Override
    protected void onStart() {
        super.onStart();
        nAuth.addAuthStateListener(firebaseAuthListener);
    }
 // Stop the activity when w leave
    @Override
    protected void onStop() {
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
