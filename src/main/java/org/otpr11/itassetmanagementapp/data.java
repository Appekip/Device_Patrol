package org.otpr11.itassetmanagementapp;

public class data {

    String user;
    String location;
    String device;
    int id;

    public data(int i, String u, String l, String d){
        id = i;
        user = u;
        location = l;
        device = d;

    };

    public String toString(){
        return id + " " + user + " "  + location + " " + device;
    }
    public String getUser(){
        return user;
    };
    public String getDevice(){
        return device;
    };public String getLocation(){
        return location;
    };public String getId(){
        return ""+ id;
    };



}
