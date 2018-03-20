package com.example.sammay.loginactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Admin extends AppCompatActivity implements View.OnClickListener {

    //a listview to see what students are registered
    ListView lvStudentsName, lvLecturersName;
    ArrayAdapter<String> adapter;
    ArrayList<String> studentarray = new ArrayList<>();
    ArrayList<String> lecturerArray = new ArrayList<>();
    Button btAddNewLecturer, btGoLecturerArea;
    EditText etLecturerName, etLecturerPassword;
    DatabaseReference fbUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        fbUsers = FirebaseDatabase.getInstance().getReference("Users");

        btGoLecturerArea = findViewById(R.id.buttonGotoLecturerAcc);

        //allow this method to be available
        addNewLecturer();
        goLecturerArea();
    }

    private void goLecturerArea(){
        btGoLecturerArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendToLecturerArea = new Intent(Admin.this, Lecturer_account.class);
                startActivity(sendToLecturerArea);
            }
        });
    }

    //This is the logic for what will happen when the button is clicked
    private void addNewLecturer() {

        //locate the buttonAddLecturer ID and set it on click listener for this class
        btAddNewLecturer = findViewById(R.id.buttonAddLecturer);
        btAddNewLecturer.setOnClickListener(this);

        //set this button on click listener, whenever it is clicked then do this
        findViewById(R.id.buttonAddLecturer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //create a new View object from this class that has layout which is inflated ontop of the class
                //that layout design drawn upon activity_add_lecturer
                View addLecturerActivityDialog =
                        LayoutInflater.from(
                                Admin.this)
                                .inflate(R.layout.activity_add_lecturer, null);

                //the variables within the addLecturerActivityDialog
                etLecturerName = addLecturerActivityDialog.findViewById(R.id.editTextLecturerName);
                etLecturerPassword = addLecturerActivityDialog.findViewById(R.id.editTextLecturerPassword);
                //The save button so to store the info to fb
               btAddNewLecturer = findViewById(R.id.buttonSaveLecturerDB);


                //The layout animation shall be an alertDialog like a pop up menue
                AlertDialog.Builder addLecturerBuilder =
                        new AlertDialog.Builder(Admin.this);

                //sets the message
                addLecturerBuilder.setMessage("Lecturer Account Creation");
                //takes the layout design object created above called addLecturerActivityDialog
                addLecturerBuilder.setView(addLecturerActivityDialog);
                //when user clicks ok button have a click listenever event and do the following
                addLecturerBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // save the credentials to the listview
                        // lecturer has option to manually register for attendance a student
                        // in case the subject cannot tap to register
                        //create a new string to store the user input text
                        String lecturername = etLecturerName.getText().toString();
                        String lecturerPassword = etLecturerPassword.getText().toString();

                        //create a new key so each user is unique under the json tree fbUsers
                        String uniqueKey = fbUsers.push().getKey();

                        //take the lecturer class constructor and create a new object and have it store these two
                        NewLecturerAccounts sebastianHunt = new NewLecturerAccounts(
                                lecturername, lecturerPassword
                        );
                        //with constructor at place, create a child under Users json tree called Lecturers
                        //under that create another child with a unique key so each user is unique
                        //under that set the value as the object created from the lecturer class constructor
                        fbUsers.child("Lecturers").child(uniqueKey).setValue(sebastianHunt);
                    }
                });
                //so user can cancel when clicking button cancel
                addLecturerBuilder.setNegativeButton("cancel", null);
                //the pop menu cannot be cancelled when clicking outside the box
                addLecturerBuilder.setCancelable(false);
                //instantiate the new menu pop up to be created
                AlertDialog newLecturerDialog = addLecturerBuilder.create();
                //once created then allow it to be seen by calling show
                newLecturerDialog.show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //whenever this button with this id is clicked call the function below
            case R.id.buttonAddLecturer:
                addNewLecturer();
                break;

            case R.id.buttonGotoLecturerAcc:
                goLecturerArea();
                break;
        }
    }
}
