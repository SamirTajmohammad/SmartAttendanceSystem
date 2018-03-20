/*
* This java class is linked with the activity file:
*       activity_add_student.xml
* whenever the save button db is clicked then do the following
* so far nothing
*
 */

package com.example.sammay.loginactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Add_student extends AppCompatActivity implements View.OnClickListener {
    Button btSaveDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        btSaveDB = findViewById(R.id.buttonSaveStudentDB);
        btSaveDB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        findViewById(R.id.buttonSaveStudentDB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }



}

