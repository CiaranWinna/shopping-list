package com.example.shopping_list;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    /**
     * variables that hold the add, edit, delete functionality
     */
    private Button add_btn;
    private Button edit_btn;
    private Button delete_btn;
    /**
     * varible that weill be used to show deletion successful message
     */
    private Toast delete_list_toast;
    private Toast upadate_list_taost;
    /**
     * count is used as varibles that is passed through the intents
     */
    private int count;
    /**
     * arraylist that will hold the customView list items
     */
    private ArrayList<CustomItem> al_items;
    /**
     * Adapter which will alter the arraylist when changes to the list have ocured
     */
    private CustomArrayAdapter caa;
    // private fields for the database
    private TestDBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private TextView list_message_update;
    private ListView list_display;
    private int database_version;

    /**
     * Oncreate message that will be called when the app is launched. The method does the following:
     * Creates database
     * Pulls in Views from the linked XML
     * Creates an ArrayList along with its adapter
     * sets the various actions that will take place when an action is carried oit on the three buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // initialise the count to zero
        count = 0;
        database_version = 1;

        // get access to an sqlite database
        tdb = new TestDBOpenHelper(this, "shoppingList.db", null, database_version);
        sdb = tdb.getWritableDatabase();

        // get the list_display view
        list_display = findViewById(R.id.list_display);
        list_message_update = findViewById(R.id.list_message_update);

        upadate_list_taost = Toast.makeText(getApplicationContext(), "List was updated!", Toast.LENGTH_LONG);

        // generate an array list with some simple strings
        al_items = new ArrayList<CustomItem>();

        // create an array adapter for al_strings and set it on the listview
        caa = new CustomArrayAdapter(this, al_items);
        list_display.setAdapter(caa);

        updateListViewFromDB();


        // add a listener to the button that will launch the add activity and
        // will attach the count to the intent
        add_btn = (Button) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            // overridden method to handle the button click
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, AddActivity.class);
                intent.putExtra("count", count);
                startActivityForResult(intent, 16);
            }
        });

        // add a listener to the button that will launch the edit activity and
        // will attach the count to the intent
        edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new View.OnClickListener() {
            // overridden method to handle the button click
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                intent.putExtra("count", count);
                startActivityForResult(intent, 16);
            }
        });

        // add a listener to the button that will launch the delete activity and
        // will attach the count to the intent
        //Deletion toast message creation
        delete_list_toast = Toast.makeText(getApplicationContext(), "List was deleted!", Toast.LENGTH_LONG);
        delete_btn = (Button) findViewById(R.id.delete_btn);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            // overridden method to handle the button click
            public void onClick(View v) {
                deleteClickHandler();
                appReset();
            }
        });
    }

    /**
     * This method will end the current activity and restart it to act as a refresh to update the Current page.
     */
    private void appReset(){
        finish();
        startActivity(getIntent());
    }

    /**
     * This method will print a log message containing the returned value to the console and call the appReset() method.
     * @param request
     * @param result
     * @param data
     */
    // overridden method that will be called whenever an intent has been returned from
    // an activity that was started by this activity.
    protected void onActivityResult(int request, int result, Intent data) {

        // check the request code for the intent and if the result was ok. if both
        // are good then take a copy of the updated count variable
        if(request == 16 && result == RESULT_OK) {
            count = data.getIntExtra("count", 0);
            Log.i("MainActivity", "count is " + count);
            appReset();
            upadate_list_taost.show();

        }
    }

    /**
     * This method will:
     * run a delete query on the loaded database(shoppingList)
     * increment the database variable that will be used to create a new databse
     * clear the arrayList
     * notify the adapter of the change
     * show toast message
     */
    private void deleteClickHandler(){
        sdb.execSQL("DELETE FROM shoppingList");
        database_version++;
        al_items.clear();
        caa.notifyDataSetChanged();
        delete_list_toast.show();
    }

    /**
     * This message will:
     * setup query that will gather all current items on the shopping that is saved on the database
     * add each item to the arrayList and notify the adapter
     * if no items are present on the list, display a message to the user stating so
     */
    private void updateListViewFromDB(){

        //Checking for current rows in the database
        // name of the table to query
        String table_name = "shoppingList";
        // the columns that we wish to retrieve from the tables
        String[] columns = {"ID", "ITEM_NAME"};
        // where clause of the query. DO NOT WRITE WHERE IN THIS
        String where = null;
        // arguments to provide to the where clause
        String where_args[] = null;
        // group by clause of the query. DO NOT WRITE GROUP BY IN THIS
        String group_by = null;
        // having clause of the query. DO NOT WRITE HAVING IN THIS
        String having = null;
        // order by clause of the query. DO NOT WRITE ORDER BY IN THIS
        String order_by = null;
        // run the query. this will give us a cursor into the database
        // that will enable us to change the table row that we are working with
        Cursor c = sdb.query(table_name, columns, where, where_args, group_by, having, order_by);
        c.moveToFirst();
        if (c.getCount() == 0){
            list_message_update.setText("You have no items on your list!");
            list_display.setVisibility(View.GONE);
        }
        else{
            list_message_update.setText("Items currently on your list:");
            for(int i = 0; i < c.getCount(); i++) {
                al_items.add(new CustomItem(c.getString(1)));
                caa.notifyDataSetChanged();
                c.moveToNext();
            }
            list_display.setVisibility(View.VISIBLE);
        }
    }

}
