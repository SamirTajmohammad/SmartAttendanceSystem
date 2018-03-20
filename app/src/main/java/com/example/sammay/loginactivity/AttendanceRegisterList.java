package com.example.sammay.loginactivity;

/**
 * Created by sammay on 27/02/2018.
 */

public class AttendanceRegisterList {


    String fullName;
    String attendance;



    public AttendanceRegisterList(
            String fullName, String attendance)
    {
        this.fullName = fullName;
        this.attendance = attendance;
    }

    public AttendanceRegisterList(
             String attendance)
    {
        this.attendance = attendance;
    }

    public String getAttendance(){
        return attendance;
    }

    public String getFullName(){
        return fullName;
    }



}
