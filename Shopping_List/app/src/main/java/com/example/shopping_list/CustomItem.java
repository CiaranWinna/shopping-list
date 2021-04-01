package com.example.shopping_list;

// simple class that contains a name and a date
// imports

//import java.text.SimpleDateFormat;
//import java.util.Date;

// class definition
public class CustomItem {

    // private fields of the class
    private String name;
    private int id;

    // constructor for the class
    CustomItem(String name) {
        // take a copy of the name and convert the time into a simple date format string
        this.name = name;
    }

    CustomItem(int id, String name){
        this.id = id;
        this.name = name;
    }

    // getter methods for both fields
    public String getName() { return name; }
    public int getId(){ return id; }
}
