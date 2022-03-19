package com.dreamsphere.smartdocs.Models;

public class User {

    String user_email;
    String user_company;

    public User(String user_email, String user_company) {
        this.user_email = user_email;
        this.user_company = user_company;
    }

    public User( ) {
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_company() {
        return user_company;
    }

    public void setUser_company(String user_company) {
        this.user_company = user_company;
    }
}
