package com.example.shopping_list;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AddActivity extends Activity {

    // private fields of the class
    private int count;
    private Button btn_back;

    // private fields of the class
    private TextView tv_display;
    private ListView lv_mainlist;
    private EditText et_new_strings;
    private ArrayList<CustomItem> al_items;
    private CustomArrayAdapter caa;
    private TestDBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private int database_version;

    /**
     * This method will:
     * Link to the created database
     * Get the intent from the Main Activity(ListActivity)
     * retrieve the views from the XML file
     * add the return intent action to the back button
     * add the lister action on the edit button to take in the user input and add it to the arrayList
     * notify the adapter
     * add item to the database if it does not exist yet
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        database_version = 1;

        // get access to an sqlite database
        tdb = new TestDBOpenHelper(getApplicationContext(), "shoppingList.db", null, database_version);
        sdb = tdb.getWritableDatabase();

        // get the intent that started this activity and extract the count from
        // it
        Intent intent = getIntent();
        count = intent.getIntExtra("count", 0);

        // get the button and attach a listener that will update the counter and
        // will dismiss this activity
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new OnClickListener() {
            // overridden on click method to return a result to the starter of this
            // activity
            public void onClick(View v) {
                Intent result = new Intent(Intent.ACTION_VIEW);
                count++;
                result.putExtra("count", count);
                setResult(RESULT_OK, result);
                finish();
            }
        });

        // pull the list view and the edit text from the xml
        tv_display = (TextView) findViewById(R.id.tv_display);
        lv_mainlist = (ListView) findViewById(R.id.lv_mainlist);
        et_new_strings = (EditText) findViewById(R.id.et_new_strings);

        // generate an array list with some simple strings
        al_items = new ArrayList<CustomItem>();

        // create an array adapter for al_strings and set it on the listview
        caa = new CustomArrayAdapter(this, al_items);
        lv_mainlist.setAdapter(caa);

        // add in a listener for the edit text to create new items in our list view
        et_new_strings.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionid, KeyEvent event) {

                ContentValues cv = new ContentValues();

                // if the user is done entering in a new string then add it to
                // the array list. this then notifies the adapter that the data has
                // changed and that the list view needs to be updated
                if(actionid == EditorInfo.IME_ACTION_DONE) {
                    Cursor tempCursor = sdb.rawQuery("select * from shoppingList where ITEM_NAME =?" ,new String [] {et_new_strings.getText().toString()});
                    if(tempCursor.getCount() <= 0){
                        al_items.add(new CustomItem(et_new_strings.getText().toString()));
                        caa.notifyDataSetChanged();
                        tv_display.setText("Item(s) added to list:");
                        cv.put("ITEM_NAME", et_new_strings.getText().toString());
                        sdb.insert("shoppingList", null, cv);
                    }
                    else{
                        tv_display.setText("Item is already on list!");
                    }
                    return true;
                }
                // if we get to this point then the event has not been handled thus
                // return false
                return false;
            }
        });

    }

    /**
     * This method will generate the menu and add any items to the action bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is
        // present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * This method will handle click actions on the action bar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}