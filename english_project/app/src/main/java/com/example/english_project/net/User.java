package com.example.english_project.net;

import android.util.Log;

public class User {

    private int id, myclass, partner;
    private String username, identity, name, gender;

    public User(int id, String username, String identity, String name, int myclass, String gender, int partner) {
        this.id = id;
        this.username = username;
        this.identity = identity;
        this.name = name;
        this.myclass = myclass;
        this.gender = gender;
        this.partner = partner;
    }
    //register
    public User(int id, String username){
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getIdentity() { return identity; }

    public String getName() { return name; }

    public int getMyclass() { return myclass; }

    public String getGender() { return gender; }

    public int getPartner() { return partner; }

    public void updatePartner(int num) { partner = num; Log.d("nowPartner", String.valueOf(partner));}


}