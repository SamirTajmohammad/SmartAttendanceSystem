/*
* This java class is linked with the activity file:
*       activity_add_lecturer.xml
* whenever the save button db is clicked then do the following
* so far nothing
*
 */

package com.example.sammay.loginactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Add_lecturer extends AppCompatActivity implements View.OnClickListener {
    Button btSaveDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecturer);
        btSaveDB = findViewById(R.id.buttonSaveLecturerDB);
        btSaveDB.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        findViewById(R.id.buttonSaveLecturerDB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


}

