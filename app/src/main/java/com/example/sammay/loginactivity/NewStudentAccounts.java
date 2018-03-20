package com.example.sammay.loginactivity;

/**
 * Created by sammay on 15/12/2017.
 */

public class NewStudentAccounts
{
    String studentID;
    String email;
    String fullname;


    //required default constructor
    public NewStudentAccounts()
    {

    }

    public NewStudentAccounts(
            String studentID, String email, String fullname)
    {
        this.studentID = studentID;
        this.email = email;
        this.fullname = fullname;
    }

    public NewStudentAccounts(
            String fullname, String studentID)
    {
        this.studentID = studentID;
        this.email = email;
        this.fullname = fullname;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }






}
