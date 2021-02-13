package com.coinnpursecoding.hba;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import java.util.Objects;

public class ViewActivity extends AppCompatActivity {

    TextView dateView;
    ListView myListView;

    public static String data;
    public static ArrayList<String> birthdays = new ArrayList<String>();
    mySQLiteDBHandler myDBH;
    public Typeface mTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        dateView = (TextView) findViewById(R.id.dateView);
        myListView = (ListView) findViewById(R.id.nListView);


        Intent j = getIntent();
        final Bundle b = j.getBundleExtra("firstDate");
        long transferredLong = Objects.requireNonNull(b).getLong("dateLong");
        int transferredYear = b.getInt("calYear");

        // Gets metrics of the device screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Adjusts window size based on display size
        getWindow().setLayout((int)(width * .8), (int)(height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;

        myDBH = new mySQLiteDBHandler(this);

        // If list is empty, show message
        if(MainActivity.events.size() == 0){
            dateView.setText(R.string.noBirthdaysText);
        } else {
            // String to compare dates to first date of month
            String firstDate = Formatter.formatLongDate(transferredLong, "MM/dd/yyyy");
            // ex. 10/??/????
            String month = Formatter.getDateValue(firstDate, 0);
            // ex. ??/??/2010
            String year = Formatter.getDateValue(firstDate, 2);

            // Fills the birthday ArrayList with birthdays matching the month and year
            myDBH.fillBirthdayList(month, year);

            // More formatting for the TextView
            String promptDate = Formatter.formatLongDate(transferredLong, "MMM d");
            String prompt = "Birthdays in " + Formatter.getFirstWord(promptDate);
            dateView.setText(prompt);

            mTypeface = Typeface.createFromAsset(getAssets(), "fonts/connection__ii.otf");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, birthdays){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Cast the ListView each item as text view
                    TextView item = (TextView) super.getView(position, convertView, parent);

                    // Set the Typeface/Font for the current item
                    item.setTypeface(mTypeface);

                    // Set the ListView item's text color
                    item.setTextColor(Color.parseColor("#C17EEA"));

                    // Center the item in the ListView
                    item.setGravity(Gravity.CENTER);

                    return item;
                }
            };
            // Set ListView to created ArrayList values
            myListView.setAdapter(arrayAdapter);

            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    new AlertDialog.Builder(ViewActivity.this)
                            .setIcon(R.drawable.cake_icon2)
                            .setTitle("Hold on, " + MainActivity.generateName(MainActivity.mode) + "...")
                            .setMessage("Are you sure you want to delete this birthday? How will you remember?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    data = (String) parent.getItemAtPosition(position);

                                    // Name and long required for deletion
                                    String day = data.substring(data.indexOf("(") + 1, data.indexOf(")"));
                                    data = data.substring(0, data.indexOf(" ("));

                                    // Deletes entry from database, returns a boolean value
                                    boolean success = myDBH.removeData(data, Formatter.dateToLong(day, transferredYear));

                                    // Removes entry from ListView
                                    if(success) {
                                        birthdays.remove(position);
                                        myListView.setAdapter(arrayAdapter);

                                        setResult(RESULT_OK);
                                        //finish();

                                        Toast.makeText(ViewActivity.this, "Birthday deleted!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ViewActivity.this, "Error: Something went wrong...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //Clear list when activity closes
        birthdays.clear();
    }
}