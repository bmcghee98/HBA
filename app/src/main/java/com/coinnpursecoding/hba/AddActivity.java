package com.coinnpursecoding.hba;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


public class AddActivity extends MainActivity {

    Button saveBtn;
    EditText nameEditText;
    TextView noteView;
    TextView dateView;

    mySQLiteDBHandler myDBH;
    ArrayList<String> people = new ArrayList<String>();
    
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        noteView = (TextView) findViewById(R.id.noteView);
        dateView = (TextView) findViewById(R.id.dateView);
        saveBtn = (Button) findViewById(R.id.saveBtn);

        myDBH = new mySQLiteDBHandler(this);

        Intent i = getIntent();
        final Bundle b = i.getBundleExtra("dateInfo");
        assert b != null;
        long transferredLong = b.getLong("dateLong");
        String formDate = b.getString("formDate");

        Calendar cal2 = Calendar.getInstance(Locale.getDefault());

        cal2.setTimeInMillis(transferredLong);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);



        // List to check for names that have already been entered
        if (myDBH.getDatabaseCount() != 0) {
            people = myDBH.getNameList(transferredLong);
        }

        // Sets TextView to date provided in bundle
        dateView.setText(Formatter.formatLongDate(transferredLong, "MMMM d, yyyy"));

        // Gets metrics of the device screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Adjusts window size based on display size
        getWindow().setLayout((int)(width * .8), (int)(height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);


        // When pressed, closes the window, saves data, and resets information
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameToEnter = nameEditText.getText().toString();

                boolean entered = false;

                // If EditText is empty, don't enter the name
                if (nameToEnter.equals("")) {
                    Toast.makeText(AddActivity.this, "Hey, " + generateName(MainActivity.mode) +
                            "! You didn't enter a name!", Toast.LENGTH_SHORT).show();
                } else {
                    for (int i = 0; i < people.size(); i++) {
                        // If name already exists, don't enter
                        if (nameToEnter.equals(people.get(i))) {
                            Toast.makeText(AddActivity.this, "This name already exists for this date...", Toast.LENGTH_SHORT).show();
                            entered = true;
                        }
                    }

                    // If birthday has not already been entered, add
                    if (!entered) {
                        // Adds info into database
                        boolean successful = AddData(nameToEnter, cal2.getTimeInMillis(), formDate);

                        // If successful, sends result back to main
                        if (successful) {
                            Toast.makeText(AddActivity.this, "Birthday saved!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddActivity.this, ReminderBroadcast.class);
                            intent.putExtra("name", nameToEnter);


                            // Generate a new notification for every entry
                            int requestCode = sharedPreferences.getInt("notifCode", 1);

                            Log.i("Initial request", Integer.toString(requestCode));
                            requestCode = ++requestCode;

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddActivity.this, requestCode , intent, 0);

                            // Saves the generated requestCode to be incremented for every notification
                            sharedPreferences.edit().putInt("notifCode", requestCode).apply();
                            Log.i("Final request", Integer.toString(sharedPreferences.getInt("notifCode", 0)));

                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            assert alarmManager != null;
                            
                            alarmManager.set(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), pendingIntent);

                            Log.i("Alarm scheduled for", Formatter.formatLongDate(cal2.getTimeInMillis(), "yyyyy.MMMMM.dd GGG hh:mm aaa"));
                            setResult(RESULT_OK);
                        } else {
                            Toast.makeText(AddActivity.this, "Error: Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        finish();
                        nameEditText.setText("");
                    }
                }
            }
        });

    }
}

