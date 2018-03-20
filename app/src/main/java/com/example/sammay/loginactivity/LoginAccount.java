
/* ----------------LoginAccount Activity-----------------------
*
* Here is where the user has a choice to register for
* account or login.
* If login then proceed to match user login details on
* firebase data and if match then user is sent to their
* account area.
*
* If register then send user to their register area
*
*
*           *****Use Cases Met*****
*           >loginUser
*           >signupUser
*           >logout
*           >invalidEmailPassword
*           >forgottenEmailPassword
*           ***********************
*
* -------------------------------------------------------
 */

package com.example.sammay.loginactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//Firebase and Libraries
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginAccount extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginAccount";
    DatabaseReference databaseRef;
    TextView tvInvalidLoginDetails, btSignup;
    CardView btLogin;
    EditText etPassword, etEmail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //get the node student accounts from firebase and assign it to databaseref
        databaseRef = FirebaseDatabase.getInstance().getReference("Student-Accounts");
        //required for firebase authenticating email address
        mAuth = FirebaseAuth.getInstance();
        //find objects and assign them accoridngly
        btLogin = findViewById(R.id.buttonLogin);
        btLogin.setOnClickListener(this);
        btSignup = findViewById(R.id.buttonSignup);
        btSignup.setOnClickListener(this);
        tvInvalidLoginDetails = findViewById(R.id.textViewInvalidLoginDetails);
        tvInvalidLoginDetails.setTextColor(Color.RED);
        tvInvalidLoginDetails.setVisibility(View.GONE);
        etPassword = findViewById(R.id.editTextPassword);
        etEmail = findViewById(R.id.editTextEmail);
    }

   private void loginUser() {
       //get user inputs and assign them as follows
       final String email = etEmail.getText().toString().trim();
       final String password = etPassword.getText().toString().trim();

       if (!(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))) {

        //authorise user email and password with database to get a match
           mAuth.signInWithEmailAndPassword(email, password)
                   .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                   {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task)
                       {
                           //Sign in success
                           if (task.isSuccessful()) {
                               finish();
                               //make intent and send user to student area
                               Intent sendToStudentArea = new Intent(LoginAccount.this, Student_area.class);
                               startActivity(sendToStudentArea);
                           } else {
                               Log.w(TAG, "LoginAccount:failure", task.getException());
                               Toast.makeText(LoginAccount.this, "Authentication failed.",
                                       Toast.LENGTH_SHORT).show();
                           }
                       }
                   });

           //default admin account required to create users
           if (email.contains("admin") && password.contains("password")){ //for now a easy password
               Intent sendToStudentArea = new Intent(LoginAccount.this, Admin.class);
               startActivity(sendToStudentArea);
           }
           
       }else{Toast.makeText(this, "Please Enter LoginAccount Details", Toast.LENGTH_SHORT).show();}
   }

   //send user to register area
   private void signUpUser(){
       startActivity(new Intent(this, RegisterAccount.class));
   }

   public void onClick(View view){ //what buttons will user click? switch to appropriate
        switch (view.getId()){
            case R.id.buttonLogin:
                loginUser();
                break;
            case R.id.buttonSignup:
                signUpUser();
                break;

        }
   }
}
