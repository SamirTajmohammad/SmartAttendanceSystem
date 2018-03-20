package com.example.sammay.loginactivity;

/**
 * Created by sammay on 27/02/2018.
 */

public class NewLecturerAccounts {

    String password;
    String email;
    String fullname;


    //required default constructor
    public NewLecturerAccounts()
    {

    }

    public NewLecturerAccounts(
            String lecturerID, String email, String fullname)
    {
        this.password = lecturerID;
        this.email = email;
        this.fullname = fullname;

    }

    public NewLecturerAccounts(
            String fullname, String password)
    {
        this.password = password;
        this.fullname = fullname;

    }

    public NewLecturerAccounts(
            String email)
    {
        this.email = email;
    }


    public String getLecturerID() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }




}
