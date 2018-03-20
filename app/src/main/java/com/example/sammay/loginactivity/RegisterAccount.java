/* ----------------Register Activity-----------------------
*
* Here is where the user must comply with the rules in order
* to register for an account such as securityMeasure: if password
* is strong enough or they entered correct email address. And
* system does authenticateStudentID: checking if student ID
* exists in the database.
*
*           *****Use Cases Met*****
*           >registerAccount
*           >securityMeasures
*           >authenticateStudentID
*           ***********************
*
*
*           //future feature:
*           a time based student registering
*           so late students when registereing after
*           a certain time period arae classified as
*           late.
*
* -------------------------------------------------------
 */

package com.example.sammay.loginactivity;

//android and Libraries
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

//Firebase and Libraries
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterAccount extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterAccount"; //required for log TAG
    //These are all the variables from the registration form
    EditText
            etUserID,
            etEmail,
            etfullname,
            etPassword;
    Button btSubmit;
    //Firebase db
    DatabaseReference fbRefStudentAccounts, fbRefLecturerAccounts, fbUsers;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btSubmit = findViewById(R.id.buttonSubmit);
        btSubmit.setOnClickListener(this);
        etUserID = findViewById(R.id.editTextID);
        etEmail = findViewById(R.id.editTextEmail);
        etfullname = findViewById(R.id.editTextFullname);
        etPassword = findViewById(R.id.editTextPassword);
        mAuth = FirebaseAuth.getInstance();

        //student accounts root json tree
        fbRefStudentAccounts = FirebaseDatabase.getInstance().getReference("Student-Accounts");

        //lecturer accounts root json tree
        fbRefLecturerAccounts = FirebaseDatabase.getInstance().getReference("LecturerAccounts");

        //Users json tree
        fbUsers = FirebaseDatabase.getInstance().getReference("Users");

        // getSupportActionBar().setTitle("RegisterAccount Your New Account");

    }

    //security measure of registeration process so that only students from city university is welcome
    //this is a boolean method because it will be tested later if it holds true
    private boolean isUserRegisterInputValid() {
        //string variables created from activity register
        String password = etPassword.getText().toString().trim();
        String userID = etUserID.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String fullname = etfullname.getText().toString().trim();

        //couldve done this in a loop that checks each text field for each iteration
        //but other things require time for work so this is left as it is
        //so first of all check if all textboxes are not empty
        if (!(email.isEmpty() && fullname.isEmpty() && password.isEmpty() && userID.isEmpty())) {

            //checks if user inputs first and surname
            if (!fullname.contains(" ") && (!fullname.matches("^(?=.*[A-Z].*[A-Z])(?=.*[a-z].*[a-z].*[a-z])"))) {
                etfullname.setError("Please enter first name and surname");
                return false;
            }

            //test to see if user did not enter their CITY EMAIL unlike so: mojtaba.tajmohammad.1@city.ac.uk
            if (!(email.contains(".") && email.contains("city.ac.uk"))) {
                etEmail.setError("Please enter your City Email!");
                return false;
            }

            //password regexp condition so user enters a strong password, see the error message
            if (!(password.length() > 6) &&
                    !(password.matches("^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z])"))) {
                etPassword.setError("Please enter a strong password containing at least:" +
                        " 1 capital letter, 1 number " +
                        "and password length must be over 6 characters long.");
                return false;
            }
        } else {
            //user entered everything wrong then set error message for appropriate variables
            etEmail.setError("Please enter a valid City University Email");
            etfullname.setError("Please enter first name and surname");
            etPassword.setError("Please enter a strong password");
            etUserID.setError("Please enter your University student ID");
            return false;
        }

        //when all cases do not return true then it meets the criteria of user register input as valid
        //ergo return true
        return true;
    }

//    private boolean getLecturerFB(){
//
//        //Two similar methods, get lecturer and students id depending on the creation of teh account during
//        // the registration process
//
//        //to return name and id
//        final boolean[] name = {false};
//        final boolean[] id = {false};
//
//        //first of all get lecturer input text the id and name
//        final String lecturerInputID = etUserID.getText().toString().trim();
//        final String lecturerInputName = etfullname.getText().toString().trim();
//        final String lecturerEmailInput = etEmail.getText().toString().trim();
//
//
//
//        //lecturer json tree from Users parents
//        fbUsers.child("Lecturers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//
//                    //save the child name from json tree as follows
//                    String fullnameFB = (String) userSnapshot.child("fullname").getValue();
//                    String lecturerIDFB = (String) userSnapshot.child("lecturerID").getValue();
//
//                    Log.v("Lecture name found", "LECTURE NAME : " + fullnameFB);
//                    Log.v("Lecture id found", "LECTURE id : " + lecturerIDFB);
//
//                    // now check if this value exists in the firebase
//                    //search fb json for lecturer input text if it exists
//                    if (lecturerInputName.matches(fullnameFB) && lecturerInputID.matches(lecturerIDFB)) {
//                        name[0] = true;
//                        id[0] = true;
//                        Log.v("User exists", "LECTURE  : " + name[0]);
//                        Log.v("User exists", "LECTURE  : " + id[0]);
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });
//
//        //found? then return true
//        return name[0] && id[0];
//    }

    //adds new lecturer accounts to fb under LecturerAccounts json
    //essentially adds the user input email text field to the correct lecturer's path in json
//    private boolean addFB_LecturerAccounts() {
//        final boolean[] success = {false};
//        final String email = etEmail.getText().toString().trim();
//        final String lecturerID = etUserID.getText().toString().trim();
//        final String fullname = etfullname.getText().toString().trim();
//
//        //put it under lecturer's json path
//        fbUsers.child("Lecturers");
//
//        if (isUserRegisterInputValid()) {
//            success[0] = true;
//            NewLecturerAccounts sebastian = new NewLecturerAccounts
//                    (email);
//            fbRefLecturerAccounts.setValue(sebastian);
//
//
//            Toast.makeText(getApplicationContext(), "Your account registration has been successful!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(getApplicationContext(), LoginAccount.class));
//        } else {
//            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
//            success[0] = false;
//            Toast.makeText(getApplicationContext(), "Invalid Lecturer Credentials Entered!!", Toast.LENGTH_SHORT).show();
//
////        Log.v("Success value", "iasdasdasdasd : " + success[0]);
//        }
//        return success[0];
//    }

    //adds new student accounts to FB under StudentAccounts
    private boolean addFB_StudentAccounts() {
        final boolean[] success = {false};
        final String userStudentIDInput = etUserID.getText().toString().trim();  // i couldve incorporated my own id instead of unique id on fb
        final String email = etEmail.getText().toString().trim();
        final String fullname = etfullname.getText().toString().trim();

        //create a new key
        String uniqueKey = fbRefStudentAccounts.push().getKey(); //doesnt matter if it is lecturer or student to get new key

        //each segment of each student are ordered by keys
        fbRefStudentAccounts.orderByKey();
        if (isUserRegisterInputValid()) {
            fbRefStudentAccounts.orderByKey().equalTo(userStudentIDInput);
            //  Log.v("E_VALUE", "User student ID entered : " + userStudentIDInput);
            // Log.v("E_VALUE", "database holding ID: " + fbRefStudentAccounts);
            success[0] = true;
            NewStudentAccounts mojtaba = new NewStudentAccounts
                    (userStudentIDInput, email, fullname);
            fbRefStudentAccounts.child(uniqueKey).setValue(mojtaba);
            Toast.makeText(getApplicationContext(), "Your account registration has been successful!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), LoginAccount.class));
        } else {
            // Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            success[0] = false;
            Toast.makeText(getApplicationContext(), "Invalid Student Credentials Entered!!", Toast.LENGTH_SHORT).show();

//        Log.v("Success value", "iasdasdasdasd : " + success[0]);
        }
                Log.v("Success value", "iasdasdasdasd : " + success[0]);

        return success[0];
    }

    //once authentication has proceeded then register their account
    private void registerAccount_fbEmailPassword() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        // call the method function and check if it is true
        if (addFB_StudentAccounts() /*&& addFB_LecturerAccounts()*/ )
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Sign up success
                            if (task.isSuccessful()) {
                                finish();
                                Toast.makeText(getApplicationContext(), "Your account has been successful", Toast.LENGTH_LONG).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "RegisterAccount:failure", task.getException());
                                Toast.makeText(RegisterAccount.this, "User RegisterAccount Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            //here if user clicks submit button then proceed with appropriate code
            case R.id.buttonSubmit:
                isUserRegisterInputValid();
                registerAccount_fbEmailPassword();
                break;
        }
    }
}