/*
  -----------------Student Account----------------------
*
*  This here is the core functionality of my system
*  where the real the problem is attempted, when a user taps their
*  device to an NFC tag security measures are taken
*  such as NFC password protection and reading correcting
*  NFC id number so that the system is not misused.
*
*  Student can only read to nfc tag for what the lecturer has
*
*           *****Use Cases Met*****
*           >acknowledgeNFC
*           >activateNFC_Settings
*           >securityMeasures
*           >authenticateStudentID
*           >invalidNFC
*           >invalidUser
*
*
*           verify_NFC_TAG()
*           verify_NFC_settings()
*           lockNFCTag(Tag tag)
*           getTodaysDate()
*           hasNFC()
*           ***********************
 ---------------------------------------------------
 */

package com.example.sammay.loginactivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
//used for security through obsecurity
import java.text.SimpleDateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Student_area extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "blah";
    //button and textview declaration
    Button btregisterAttendance;
    TextView textView5;
    CheckBox checkBox14;
    NfcAdapter nfcAdapter;
    DatabaseReference databaseRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_area);
        //nfc class
        //get the default nfc adapter for this device
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //verifies nfc is on
        //verify_NFC_settings();
        //required to set title for toolbar
        //getSupportActionBar().setTitle("NFC Attendance");
        //displays user ID and others if edited
        //user can only activate button by tapping phone to nfc
        //btregisterAttendance.setEnabled(false); //later enable button
        //btregisterAttendance.setClickable(false);
        //btregisterAttendance.setVisibility(View.GONE);
        textView5 = findViewById(R.id.textView5);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        //list of student names in checkboxes
        checkBox14 = findViewById(R.id.checkBox14);
        checkBox14.setClickable(false);

        btregisterAttendance = findViewById(R.id.btregisterAttendance);

        //verify if student has nfc in their phone and is enabled
        //and takes them to their nfc setting if nfc is not on
        verify_NFC_settings();

        //not necesarry for user to be registered
       // when this function is called i.e. the main button is clicked then open the calendar
        findViewById(R.id.btregisterAttendance).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder openCalendar =
                                new AlertDialog.Builder(Student_area.this);
                        View addCalendarActivityDialog =
                                getLayoutInflater()
                                        .inflate(R.layout.activity_calendar, null);
                        openCalendar.setView(addCalendarActivityDialog);
                        AlertDialog dialog = openCalendar.create();
                        dialog.show();
                    }
                }
        );
    }
////////////////////////////////////   Student registry  \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //get user CITY Email and then truncate such that it becomes into a name and surname
    //ergo email info is deleted such as "." + "city.ac.uk"
    //this is required so that we know what student has tapped nfc tag to register
    //we take their name from their email so that their attendance is recorded with their fullname
    public String getUserName(){
        String loginEmailInput = user.getEmail();
        // String studentName = map.get("fullname");
        Log.v("STUDENT----NAME", "STUDENTNAME : " + loginEmailInput);
        if (loginEmailInput.contains(".") && loginEmailInput.contains("city.ac.uk")){
           // String nameAndSurname = loginEmailInput.replaceAll("^(.)(city.ac.uk)","");
            String nameAndSurname = loginEmailInput;
            nameAndSurname = nameAndSurname.replaceAll("@city.ac.uk", "");
           // nameAndSurname = nameAndSurname.replaceAll(".", "");
            nameAndSurname = nameAndSurname.replaceAll("[0-9]","");
            //given it a space, future TODO: remove unnessary empty string at the end of nameAndSurname string
            //e.g. "mojtaba tajmohammad " here there is a space
            nameAndSurname = nameAndSurname.replace(".", " ");
            return nameAndSurname;
        }else{
            Toast.makeText(this, "You have entered a bogus email address", Toast.LENGTH_SHORT).show();
            return loginEmailInput;
        }
    }

    //save registered user to database and mark as present - all under AttendanceRegistry tree
    public void markAsPresent(){
        //if checkbox is not checked then they are not registered
        //checks if user is registered or not and marks them as appropriate and saves to fb
        if (!(checkBox14.isChecked()))
        {
            //create a new tree called AttendanceRegistry and within that push a new tree with a new day
            //get the student name (from email truncated) and set value as present

            String attendance = "Present";
            AttendanceRegisterList registerThisStudent = new AttendanceRegisterList(attendance);


            //instead of unique key have it by the days date
            databaseRef
                    .child("AttendanceRegistry")
                    .child(getTodaysDate())
                    .child(getUserName())
                    .setValue(registerThisStudent);

            //then set checkbox true and set checkbox text as the students name
            checkBox14.setChecked(true);
            checkBox14.setText(getUserName());

            //create an alertbox showing that the student is registered and their
            //attendance registering process has ended.

            AlertDialog.Builder attendanceAlert = new AlertDialog.Builder(Student_area.this);
            attendanceAlert.setMessage("You have been successfully registered to the attendance registry.").setCancelable(false)
                    .setPositiveButton("Close Application", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close everything
                            finish();
                        }
                    });
            // AlertDialog alertDialog = nfcAlert.create();
            attendanceAlert.setTitle("Student Attendance Process");
            attendanceAlert.show();
        }
    }

    //required for hidden code and know user is accessing nfc tag based on the date
    @SuppressLint("SimpleDateFormat")
    public String getTodaysDate() {
        //return the current time in millies to variable date
        long date = System.currentTimeMillis();
        //format the date as only "MMdd" and assign that to simpleDateFormat
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("MMdd");
        //assign the date as string variable
        //return date string
        return simpleDateFormat.format(date);
    }
////////////////////////////////////   NFC Codes   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //here whenever the nfc tag is tapped then these function gets triggered:
    // verify_NFC_TAG - the nfc tag is verifeid first for security purposes
    // markAsPresent - users name is transferred to the AttendanceRegistery where they are saved
    // as "Present"

    //- taken from NFC tutorial vid
    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "You just tapped to the NFC tag", Toast.LENGTH_SHORT).show();
        super.onNewIntent(intent);
            //if the nfc tag has text
            if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                Toast.makeText(
                        this, "NFC Intent recieved!", Toast.LENGTH_SHORT).show();
                //read from the nfc tag and save it to parceavle list
                Parcelable[] parcelables =
                        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if (parcelables != null && parcelables.length > 0) {
                    //call functions to read then call verify
                    readFromNFC((NdefMessage) parcelables[0]);
                        //verify if this nfc tag contains correct text
                    verify_NFC_TAG();
                    }
                } else {
                    Toast.makeText(
                            this, "No Ndef records found!", Toast.LENGTH_SHORT).show();
                }
        }
    //onResume and onPause is required so that
    //student cannot register when this application's activity is not running
    //- taken from NFC tutorial vid
    @Override
    protected void onResume() {
        super.onResume();
        //while current activity is running then enable
        enableForegroundDispatchSystem();
    }

    //- taken from NFC tutorial vid
    @Override
    protected void onPause() {
        super.onPause();
        //current activity is on pause so disable
        disableForegroundDispatchSystem();
    }

    //- taken from NFC tutorial vid
    private void enableForegroundDispatchSystem() {
       Intent intent =
               new Intent(this, Student_area.class)
                       .addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(
                this, pendingIntent, intentFilters, null);
    }

    //here the nfcadapter is disabled ergo activity is not running thus disable
    //- taken from NFC tutorial vid
    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }


    // - taken from NFC tutorial vid
    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent =
                    new String(payload, languageSize + 1,
                            payload.length - languageSize - 1,
                            textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    /*
       *how it works:
       * onNewIntent
       * when activity recieves a new intent the onNewIntent is overidden so that
       * when user taps device to nfc they write hidden messege to NFC tag
       * OnResume
       * when this activity starts the onResume method is called
       * which means any intent of NFC adapter is dispatched to Student_area
       * OnPause
       * when user leaves application, the application becomes not in active ergo outside users
       * can only read
       *
     */

    //read from the nfc tag - taken from NFC tutorial vid
    private void readFromNFC(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            textView5.setText(tagContent);
        } else {
            Toast.makeText(this, "No Ndef records found!", Toast.LENGTH_SHORT).show();
        }
    }

    // reads the NFC tag and checks if the text matches hidden code
    // if it does then student is registered.
    private void verify_NFC_TAG() {
        //apply the date to variable todaysDate
        String todaysDate = getTodaysDate();
        boolean matchingDate = textView5.getText().toString().contains(todaysDate);
        if (matchingDate) {
            Toast.makeText(
                    this,
                    "You are successfully signed in as 'Present' in the Tap4Register system!",
                    Toast.LENGTH_SHORT).show();
            //get students name
            // call the main function to register that student
            markAsPresent();
        } else {
            Toast.makeText(
                    this, "Something went wrong, tap again!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // whether the device has NFC adapter and if it is enabled
    //THIS DOES NOT WORK CONTINOUSLY LIKE SO: IT IS NOT ALWAYS RUNNING IN THE BACKGROUND
    //AND CHECKING IF NFC IS ON - I KEPT IT SIMPLE because other task have higher priority
    public void verify_NFC_settings() {
        //notify user nfc has been turned on
        NfcAdapter.getDefaultAdapter(this);
            if (hasNFC()) {
                Toast.makeText(this, "NFC has been turned on!", Toast.LENGTH_LONG).show();
            } else {
                AlertDialog.Builder nfcAlert = new AlertDialog.Builder(Student_area.this);
                nfcAlert.setMessage("Please enable your device NFC setting").setCancelable(false)
                        .setPositiveButton("Go to my device settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //send the user to their wireless settings
                                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            }
                        });
                // AlertDialog alertDialog = nfcAlert.create();
                nfcAlert.setTitle("NFC Is Not Activated");
                nfcAlert.show();

                Toast.makeText(this, "Please turn on your NFC", Toast.LENGTH_LONG).show();
            }
        }

    //testing nfc is operating - taken from NFC tutorial vid
    boolean hasNFC() {
        boolean hasFeature =
                getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
        boolean isEnabled = NfcAdapter.getDefaultAdapter(this).isEnabled();
        return hasFeature && isEnabled;
    }

    // required so that no third party app can write to tag
//    public boolean lockNFCTag(Tag tag) {
//        // if the tag contains no memory then return
//        if (tag == null) {
//            return false;
//        }
//        try {
//            //get the messege in the tag and assign it as ndef
//            Ndef ndef = Ndef.get(tag);
//            //if the messege is null
//            if (ndef != null) {
//                //connect to the tag
//                ndef.connect();
//                // if the tag can be read only
//                if (ndef.canMakeReadOnly()) {
//                    ndef.canMakeReadOnly();
//                    return true;
//                }
//            }
//            //disable io operations
//            assert ndef != null;
//            ndef.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }

    //user clicks that and do that
    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btregisterAttendance:
//                registerAttendance();
//                break;
//        }
    }
}
