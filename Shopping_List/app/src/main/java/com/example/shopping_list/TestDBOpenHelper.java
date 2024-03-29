package com.example.shopping_list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
public class TestDBOpenHelper extends SQLiteOpenHelper {
    // constructor for the class here we just map onto the constructor of the
    // super class
    public TestDBOpenHelper(Context context, String name, CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
    }

    // overridden method that is called when the database is to be created
    public void onCreate(SQLiteDatabase db) {

        // create the database
        db.execSQL(create_table);
    }

    // overridden method that is called when the database is to be upgraded
    // note in this example we simply reconstruct the database not caring for
    // data loss ideally you should have a method for storing the data while you
    // are reconstructing the database
    public void onUpgrade(SQLiteDatabase db, int version_old, int version_new) {

        // drop the tables and recreate them
        db.execSQL(drop_table);
        db.execSQL(create_table);
    }

    // a bunch of constant strings that will be needed to create and drop
    // databases
    private static final String create_table = "create table shoppingList (" +
            "ID integer primary key autoincrement, " +
            "ITEM_NAME string UNIQUE" +
            ")";


    private static final String drop_table = "drop table shoppingList";
}