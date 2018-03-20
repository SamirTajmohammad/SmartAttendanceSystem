package com.example.sammay.loginactivity;

//import android.app.ListActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//
//import java.util.ArrayList;
//
///**
// * Created by sammay on 02/01/2018.
// */
//
//public class ListViewStudent extends ListActivity {
//
//    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
//    ArrayList<String> listItems=new ArrayList<String>();
//
//    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
//    ArrayAdapter<String> adapter;
//
//    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
//    int clickCounter=0;
//
//    @Override
//    public void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
//        setContentView(R.layout.activity_lecturer_account);
//        adapter=new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                listItems);
//        setListAdapter(adapter);
//    }
//
//    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
//    public void addItems(View v) {
//        adapter.add("New Item");
//    }
//}
//
