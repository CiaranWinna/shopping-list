package com.example.shopping_list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditActivity extends Activity {

    // private fields of the class
    private int count;
    private Button btn_back;

    // private fields of the class
    private TextView tv_display;
    private ListView lv_mainlist;
    //private Button del_button;
    //private EditText et_new_strings;
    private ArrayList<CustomItem> al_items;
    private CustomArrayAdapter caa;
    private TestDBOpenHelper tdb;
    private SQLiteDatabase sdb;
    private int database_version;
    private Toast del_item_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // get the intent that started this activity and extract the count from
        // it
        Intent intent = getIntent();
        count = intent.getIntExtra("count", 0);

        //get xml views
        tv_display = findViewById(R.id.tv_display);
        lv_mainlist = findViewById(R.id.lv_mainlist);

        // initialise the count to zero
        count = 0;
        database_version = 1;

        // creating delete toast message confirmation

        del_item_message = Toast.makeText(getApplicationContext(), "List was deleted!", Toast.LENGTH_LONG);

        // get access to an sqlite database
        tdb = new TestDBOpenHelper(getApplicationContext(), "shoppingList.db", null, database_version);
        sdb = tdb.getWritableDatabase();

        // generate an array list with some simple strings
        al_items = new ArrayList<CustomItem>();

        // create an array adapter for al_strings and set it on the listview
        caa = new CustomArrayAdapter(this, al_items);
        lv_mainlist.setAdapter(caa);

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
            tv_display.setText("You have no items to edit!");
        }
        else{
            for(int i = 0; i < c.getCount(); i++) {
                al_items.add(new CustomItem(c.getString(1)));
                caa.notifyDataSetChanged();
                c.moveToNext();
            }
        }

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



        // add in a listener that listens for short clicks on our list items
        lv_mainlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // overridden method that we must implement to get access to short clicks
            public void onItemClick(AdapterView<?> adapterview, View view, int pos, long id) {

                simpleInputDialog(pos);
            }
        });

        // add in a listener that listens for long clicks on our list items
        lv_mainlist.setOnItemLongClickListener(new AdapterView .OnItemLongClickListener() {
            // overridden method that we must implement to get access to long clicks
            public boolean onItemLongClick(AdapterView<?> adapterview, View view, int pos, long id) {
                CustomItem temp = al_items.get(pos);
                String temp_name = temp.getName();
                if(deleteItem(temp_name, pos) == true){
                    del_item_message.show();
                }
                else{
                    tv_display.setText("Could not delete item from cart, please try again!");
                }
                return true;
            }

        });

    }

    // private method that will build a simple input dialog
    private void simpleInputDialog(final int pos) {

        CustomItem temp = al_items.get(pos);
        final String temp_name = temp.getName();
        // we need a builder to create the dialog for us
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set the title on this dialog
        builder.setTitle("Set a new name for the item (" + temp_name +") :");

        // it is possible to define your own layouts on a dialog but because we only need
        // a single edit text we
        // will create it and add it here
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setHint("New name");
        builder.setView(et);

        // add in the positive button
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean is_edited = editItem(temp_name, et.getText().toString(), pos);
                if (is_edited == true){
                    // we know for definite that the user has clicked the yes button
                    Toast.makeText(EditActivity.this, "Item name changed to " + et.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(EditActivity.this, "Item name could not be changed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // add in the negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // we know for definite that the user has clicked the yes button
                Toast.makeText(EditActivity.this, "Changes not saved!", Toast.LENGTH_SHORT).show();
            }
        });

        // create the dialog and display it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean deleteItem (String i, int pos){
        al_items.remove(pos);
        caa.notifyDataSetChanged();
        sdb.execSQL("DELETE FROM shoppingList WHERE ITEM_NAME = '" + i + "'");
        return true;

    }

    private boolean editItem (String o , String n, int p){
        sdb.execSQL("UPDATE shoppingList SET ITEM_NAME = '" + n + "' WHERE ITEM_NAME = '" + o + "'");
        al_items.set(p, new CustomItem(n));
        caa.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is
        // present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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