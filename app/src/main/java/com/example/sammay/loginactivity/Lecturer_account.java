/* ----------------Lecture Account-----------------------
*
* The lecturer must first tap to a new NFC tag,
* the nfc tag will be read if it contains text then
* rejected, otherwise it will write text and system
* simulataneously locks the tag after.
*
* The lecturer decides what nfc for students to
* tap and register, and what students are allowed to sign
* in for a account, if they are students of the university.
*
* The lecturer also gets the students fullname those that tapped on the nFC to register
* essentailly those who are marked as present on FB
*
//        /* 1. The lecturer creates new student ID
//         * 2. Student enters their ID
//         * 3. App checks for a match
//         * 3a. if there is a match, their account is created
//         * 3b. else invalid ID, then account is not created
//         * 4. Lecturer writes and locks a new nfc tag
//         * 5. NFC tag awaiting to be read only.
//
*           *****Use Cases Met*****
*           >createNewStudent
*           >studentAlreadyExists
*           >viewStudentAttendanceRecord
*           >generateRndString()
*           ***********************
*
* -------------------------------------------------------
 */


package com.example.sammay.loginactivity;
//android and Libraries
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//Firebase and Libraries
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//java and Libraries
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Locale;

public class Lecturer_account extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "blah";
    Student_area studentArea = new Student_area();

    //Firebase
    DatabaseReference databaseReference, fbUsers, dbAttendanceRegistry;

    //Android layout
    Button btAddNewStudent, btAddStudent, btRefreshRegistry, btSaveToFile;
    EditText etStudentName, etStudentUserID;
        TextView textView6, textView3;

    //NFC
    NfcAdapter nfcAdapter;

    //create a path where the records are saved
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/studentAttendanceRecord";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_account);

        //Firebase
       // databaseReference = FirebaseDatabase.getInstance().getReference("StudentListView");
        fbUsers = FirebaseDatabase.getInstance().getReference("Users");
        dbAttendanceRegistry = FirebaseDatabase.getInstance().getReference("AttendanceRegistry");

        //Android layout
        btRefreshRegistry = findViewById(R.id.buttonRefresh);
        btRefreshRegistry.setOnClickListener(this);
        textView3 = findViewById(R.id.textView3);
        btSaveToFile = findViewById(R.id.buttonSave);


        //NFC
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        final Student_area student_area = new Student_area();

        btRefreshRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAttendanceRegistry.child(student_area.getTodaysDate()).push().setValue("watermalon");
            }
        });



        dbAttendanceRegistry.child(studentArea.getTodaysDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Create a string builder to add many students name
                    //because normal string will create a new object changing it
                    //whereas string builder is mutable and allows appending new strings
                    StringBuilder s = new StringBuilder(100);
                    for(DataSnapshot d : dataSnapshot.getChildren()) {

                        String studentName = d.getKey();
                        //add studentName with new line so second name appended will have a new line before it
                        s.append(studentName + "\n");
                        //how the stringBuilder was created with new lines - take that and setText
                        textView3.setText(s);
                    }
                }
            }//onDataChange

            @Override
            public void onCancelled(DatabaseError error) {

            }//onCancelled
        });


        addNewStudent();

        //call and instantiate
        File directory = new File(path);
        //create a new file using make directory
        directory.mkdir();
    }

///////////////////////////////////////   Student Registry code   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    //for every student that taps to the nfc tag - they become registered for attendance
    // their fullname is recorded and lecturer creates a new json tree for each student under
    // AttendanceRegistry and the lecturer retrieves this data and shown in a listview format

    public void saveAttendanceToFile(View view){

        //create a new file under path with the days date
        File file = new File(path + studentArea.getTodaysDate() + ".txt");
        //string array to save the names from the text, everytime there is a line go to next arrary
        String [] saveAttendanceRecord = String.valueOf(textView3.getText()).split(System.getProperty("line.separator"));

        SaveFile (file, saveAttendanceRecord);

    }

    //code taken from:
    //https://mega.nz/#!JJpTAKoD!t021Az4QxhWkajL1rjxsoWgbNVthK-pOAXqJzE4plOQ
    public static void SaveFile(File file, String[] data)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length; i++)
                {
                    fos.write(data[i].getBytes());
                    if (i < data.length-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }

    //required for adding random text to nfc tag for security through obsecurity
    public String generateRndString() {
        //get all alphabets in caps and lowers and symbols and numbers
        String regexString =
                "^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z])";
        //taken from java security lib, get random and call it rnd
        SecureRandom rnd = new SecureRandom();
        //i want the string to be of length 6
        int rndStringLength = 6;
        //a string builder to store 6 places
        StringBuilder sb = new StringBuilder(rndStringLength);
        for (int i = 0; i < rndStringLength; i++)
            //get the length of ragexString, then take the random of next Int from ragexString
            //length take that char position and append it to string builder
            sb.append(regexString.charAt(rnd.nextInt(regexString.length())));
        //keep doing this and return the string builder to method generateRndString
        return sb.toString();
    }


    // first lecturer must add a student to the registry so student can register for account
    //alternative use case TODO: student already exists
    public void addNewStudent() {


        btAddNewStudent = findViewById(R.id.buttonAddStudent);
        btAddNewStudent.setOnClickListener(this);

        findViewById(R.id.buttonAddStudent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View addStudentActivityDialog =
                        LayoutInflater.from(
                                Lecturer_account.this)
                                .inflate(R.layout.activity_add_student,null);

                etStudentName = addStudentActivityDialog.findViewById(R.id.editTextStudentName);
                etStudentUserID = addStudentActivityDialog.findViewById(R.id.editTextStudentUserID);
                btAddStudent = findViewById(R.id.buttonSaveLecturerDB);

                AlertDialog.Builder addStudentBuilder =
                        new AlertDialog.Builder(Lecturer_account.this);

                addStudentBuilder.setMessage("Student Attendance Record");
                addStudentBuilder.setView(addStudentActivityDialog);

                addStudentBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String studentName = etStudentName.getText().toString();
                        String studentID = etStudentUserID.getText().toString();
//                        if (!(studentarray.contains(studentName)
//                                || (studentarray.contains(studentID)))){
//
//                            // databaseReference. ("studentName").setValue(studentName);
//                            databaseReference.child("studentID").setValue(studentID);
//
//                            Toast.makeText(
//                                    Lecturer_account.this,
//                                    "New Student added to Record",
//                                    Toast.LENGTH_SHORT).show();
//                        }else{
//                            Toast.makeText(
//                                    getApplicationContext(),
//                                    "Student with that ID already exists in Record!",
//                                    LENGTH_SHORT).show();
//                        }




                        String uniqueKey = fbUsers.push().getKey();
                        NewStudentAccounts mojtabaTaj = new NewStudentAccounts(
                                studentName, studentID
                        );
                        fbUsers.child("Students").child(uniqueKey).setValue(mojtabaTaj);

                    }
                });
                addStudentBuilder.setNegativeButton("cancel", null);
                addStudentBuilder.setCancelable(false);
                AlertDialog newStudentDialog = addStudentBuilder.create();
                newStudentDialog.show();
            }
        });
    }


////////////////////////////////////   NFC Codes   \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(
                this, "Lecturer NFC intent recieved", Toast.LENGTH_SHORT).show();
        super.onNewIntent(intent);

        //if their nfc tag is not null and is enabled
//        studentArea.verify_NFC_settings();

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);

        //if nfc tag is not null and is enabled
        //Toast.makeText(this, "NFC Intent!", Toast.LENGTH_SHORT).show();


        //write to the tag
        //hide the dateString with two random generated strings
        NdefMessage ndefMessage;
        ndefMessage = createNdefMessage(
                generateRndString()
                        + "#" + studentArea.getTodaysDate()
                        + "#" + generateRndString());
        writeNdefMessage(tag, ndefMessage);

        //after tag has been written lecturer locks nfc tag
//        try {
//            ndef.makeReadOnly();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        //while current activity is running then true
        enableForegroundDispatchSystem();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //current activity is on pause so false

        disableForegroundDispatchSystem();
    }

    private void enableForegroundDispatchSystem() {

        //send to lecturer activity
        Intent intent = new Intent(
                this, Lecturer_account.class)
                .addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[] {};

        nfcAdapter.enableForegroundDispatch(
                this, pendingIntent, intentFilters, null);

    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);

    }

    //check if tag is formattable then format the ndefmessage
    public void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(
                        this, "Tag is not NDEF formatable", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag written!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTAG", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payLoad =
                    new ByteArrayOutputStream(1 + languageSize + textLength);

            payLoad.write((byte) (languageSize & 0x1F));
            payLoad.write(language, 0, languageSize);
            payLoad.write(text, 0, textLength);

            return new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT,
                    new byte[0],
                    payLoad.toByteArray());
        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    public NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = createTextRecord(content);

        return new NdefMessage(new NdefRecord[]{ndefRecord});

    }


    public void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {

        //check if tag is not null
        try {
            if (tag == null) {
                Toast.makeText(
                        this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }
            //get what is inside the tag and call it ndef
            Ndef ndef = Ndef.get(tag);

            //if what is inside the tag's ndef not null
            if (ndef == null) {
                //format tag with the ndef format and write the message
               formatTag(tag, ndefMessage);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(
                            this, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag is written", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }
    }

    //read from the nfc tag
    private void readFromNFC(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = studentArea.getTextFromNdefRecord(ndefRecord);
            textView6.setText(tagContent);
        } else {
            Toast.makeText(this, "No Ndef records found!", Toast.LENGTH_SHORT).show();
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAddStudent:
                addNewStudent();
                break;

        }
    }
}

