package com.example.english_project.net;

public class User {

    private int id, myclass;
    private String username, identity, name, gender;

    public User(int id, String username, String identity, String name, int myclass, String gender) {
        this.id = id;
        this.username = username;
        this.identity = identity;
        this.name = name;
        this.myclass = myclass;
        this.gender = gender;
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

}