package com.coinnpursecoding.hba;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class DayActivity extends AppCompatActivity {

    TextView dateView;
    ListView nListView;

    mySQLiteDBHandler mySQLiteDBHandler;

    public static String data;

    Typeface mTypeface;

    ArrayList<String> people = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        dateView =  (TextView) findViewById(R.id.dateView);
        nListView = (ListView) findViewById(R.id.nListView);

        mySQLiteDBHandler = new mySQLiteDBHandler(this);

        // Gets metrics of the device screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Adjusts window size based on display size
        getWindow().setLayout((int)(width * .8), (int)(height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;


        Intent i = getIntent();
        final Bundle b = i.getBundleExtra("selectedDate");
        assert b != null;
        long transferredLong = b.getLong("date");

        mySQLiteDBHandler mySQLiteDBHandler = new mySQLiteDBHandler(this);

        dateView.setText(Formatter.formatLongDate(transferredLong, "MMMM d, yyyy"));

        // Gets names of people for that particular day
        people = mySQLiteDBHandler.getNameList(transferredLong);

        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/connection__ii.otf");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, people) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position, convertView, parent);

                // Set the typeface/font for the current item
                item.setTypeface(mTypeface);

                // Set the list view item's text color
                item.setTextColor(Color.parseColor("#C17EEA"));

                // Center the item in the ListView
                item.setGravity(Gravity.CENTER);

                return item;
            }
        };

        // Set ListView to created ArrayList values
        nListView.setAdapter(arrayAdapter);

        nListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(DayActivity.this)
                        .setIcon(R.drawable.cake_icon2)
                        .setTitle("Hold on, " + MainActivity.generateName(MainActivity.mode) + "...")
                        .setMessage("Are you sure you want to delete this birthday? How will you remember?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data = (String) parent.getItemAtPosition(position);

                                // Deletes entry from database, returns a boolean value
                                boolean success = mySQLiteDBHandler.removeData(data, transferredLong);

                                // Removes entry from ListView
                                if(success) {
                                    people.remove(position);
                                    nListView.setAdapter(arrayAdapter);

                                    setResult(RESULT_OK);
                                    //finish();

                                    Toast.makeText(DayActivity.this, "Birthday deleted!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DayActivity.this, "Error: Something went wrong...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Clear list when activity closes
        people.clear();
    }
}