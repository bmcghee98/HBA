package com.coinnpursecoding.hba;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.app.AlarmManager;
import android.app.DatePickerDialog;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;


import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;


import java.util.ArrayList;
import java.util.Calendar;


import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;


import static com.coinnpursecoding.hba.Formatter.formatLongDate;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    CardView cardView;
    TextView greetingView;
    Button dateBtn;
    Button settingsBtn;
    Button addBtn;
    Button viewBtn;
    public CalendarView calendarView;

    DatePickerDialog.OnDateSetListener mDateSetListener;

    mySQLiteDBHandler myDatabase;
    public static SharedPreferences sharedPreferences;

    public static List<EventDay> events = new ArrayList<>();
    public static String mode;
    public String greetingStr;

   public static String[] meanNames = {"stupid", "jerk", "dunce", "dipstick", "bonehead", "dingbat", "dork",
            "imbecile", "cretin", "noob", "butthead", "clown", "degenerate", "donkey", "hooligan",
            "lamebrain", "loser", "nerd", "scumbag", "snotball", "turd", "twit", "wacko", "weirdo",
            "worm", "bozo", "bum", "chicken", "egg", "fart"};

    public static String[] niceNames = {"honey", "sweetheart", "gorgeous", "lovely", "muffin", "sweetie", "bean",
            "precious", "sunshine", "sugar", "darling", "angel", "star", "sweet pea", "beloved", "peach",
            "friend", "dude", "pal", "boss", "buttercup", "nugget", "BFF", "bestie", "bubba", "buddy",
            "boo", "champ", "cookie", "bubbles", "bunny", "captain", "cutie", "dear", "dandelion"};

    // Bullies or loves the user
    public static String generateName(String mode){
        Random rand = new Random();
        int randomNum;
        String name = "";

        // Switch case dependent on SharedPreferences
        switch(mode){
            case "Mean":
                randomNum = rand.nextInt(meanNames.length);
                name = meanNames[randomNum];
                break;
            case "Nice":
                randomNum = rand.nextInt(niceNames.length);
                name = niceNames[randomNum];
                break;
            default:
                break;
        }

        return name;
    }

    // Adds dateLong to SQLite Database
    public boolean AddData(Birthday person){
            return myDatabase.addData(person);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = (CalendarView) findViewById(R.id.calendar);
        textView = (TextView) findViewById(R.id.textview);
        cardView = (CardView) findViewById(R.id.cardView);
        greetingView = (TextView) findViewById(R.id.greetingText);
        settingsBtn = (Button) findViewById(R.id.settingsBtn);
        addBtn = (Button) findViewById(R.id.addBtn);
        viewBtn = (Button) findViewById(R.id.viewBtn);
        dateBtn = (Button) findViewById(R.id.dateBtn);

        myDatabase = new mySQLiteDBHandler(this);
        final Bundle a = new Bundle();

        //If database is not empty, get every birthday long and display to calendarView
        if(myDatabase.getDatabaseSize() != 0) {
            myDatabase.getLongData(events);
            calendarView.setEvents(events);
        }

        sharedPreferences = this.getSharedPreferences("com.coinnpursecoding.hba", Context.MODE_PRIVATE);
        createNotificationChannel();

        // This is for when the app first installs and there is no mode saved
        if(sharedPreferences.getString("mode", "").equals("")) {
            // Default mode is mean
            sharedPreferences.edit().putString("mode", "Mean").apply();
        }

        // This is for when the app first installs and there is no request code for notifications
        // Request code is used to generate a new notification after every birthday added
        if(sharedPreferences.getInt("notifCode", 0) == 0){
            sharedPreferences.edit().putInt("notifCode", 1).apply();
        }

        // Sets the greeting name to whatever mode is set(mean or nice)
        mode = sharedPreferences.getString("mode", "");
        greetingStr = "Greetings, " + generateName(mode) +". \nWho did you forget today?";
        greetingView.setText(greetingStr);

        // Makes the calendar appear
        cardView.setAlpha(0);
        cardView.animate().alphaBy(1).setDuration(1500);
        cardView.isClickable();

        final Calendar cal = Calendar.getInstance(Locale.getDefault());

        // When button is pressed, brings up the DatePickerDialog
        dateBtn.setOnClickListener(v -> {

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog, mDateSetListener, year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        // Adjusts the calendar based on the dialog box, also assigns TextView above the calendar
        mDateSetListener = (view, year, month, dayOfMonth) -> {
            //Clear bundle before setting it
            a.clear();

            // Sets Calendar to dialog input
            cal.set(year, month, dayOfMonth);

            // Sets text below calendar to selected date
            String textViewStr = Formatter.formatLongDate(cal.getTimeInMillis(), "MM/dd/yyyy");
            textView.setText(textViewStr);

            // Makes the addBtn appear
            addBtn.animate().alphaBy(1).setDuration(1000);
            addBtn.isClickable();

            // Sets CalendarView to date chosen by user
            try {
                calendarView.setDate(cal);
                Log.i("Date changed", Long.toString(cal.getTimeInMillis()));
            } catch (Exception e){
                e.printStackTrace();
            }

            // Bundles date to send to AddActivity window
            a.putLong("dateLong", cal.getTimeInMillis());
            a.putString("slashedDate", textView.getText().toString());

        };
        // Controls changing the date through the CalendarView
            calendarView.setOnDayClickListener(eventDay -> {
                //Clear bundle before setting it
                a.clear();
                // Assigns EventDay to a Calendar
                Calendar clickedDay = eventDay.getCalendar();

                // If a birthday exists for selected day, starts the DayActivity
                for (int i = 0; i < events.size(); i++){
                    if (events.get(i).getCalendar() == clickedDay){
                        Intent k = new Intent(MainActivity.this, DayActivity.class);
                        a.putLong("date", clickedDay.getTimeInMillis());
                        k.putExtra("selectedDate", a);
                        startActivityForResult(k, 3);
                    }
                }

                // Assigns TextView above the calendar
                textView.setText(formatLongDate(clickedDay.getTimeInMillis(), "MM/dd/yyyy"));

                // AddBtn appears after initially changing the date
                addBtn.animate().alphaBy(1).setDuration(1000);
                addBtn.isClickable();

                // Bundles date to send to AddActivity
                a.putLong("dateLong", clickedDay.getTimeInMillis());
                a.putString("slashedDate", textView.getText().toString());
            });


        // When pressed, brings up AddActivity
        addBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AddActivity.class);
            i.putExtra("dateInfo", a);

            startActivityForResult(i, 1);
        });

        // When pressed, brings up ViewActivity
        viewBtn.setOnClickListener(v -> {
            Intent j = new Intent(MainActivity.this, ViewActivity.class);
            a.putLong("dateLong", calendarView.getCurrentPageDate().getTimeInMillis());
            a.putInt("calYear", cal.get(Calendar.YEAR));
            j.putExtra("firstDate", a);

            startActivityForResult(j, 2);
        });

        // When pressed, brings up SettingsActivity
        settingsBtn.setOnClickListener(v -> {
            Intent k = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(k, 4);
        });

    }

    // Adds birthday to events, then sets it on the CalendarView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Adds an event marker to CalendarView
        if(requestCode == 1 && resultCode == RESULT_OK) {
            events.clear();
            myDatabase.getLongData(events);
            calendarView.setEvents(events);
        }
        //Removes deleted birthdays from CalendarView
        if(requestCode == 2 && resultCode == RESULT_OK){
            events.clear();
            // If-statement so app does not crash when database is empty
            if(myDatabase.getDatabaseSize() != 0) {
                myDatabase.getLongData(events);
            }
            calendarView.setEvents(events);
        }
        // Same thing
        if(requestCode == 3 && resultCode == RESULT_OK){
            events.clear();
            // If-statement so app does not crash when database is empty
            if(myDatabase.getDatabaseSize() != 0) {
                myDatabase.getLongData(events);
            }
            calendarView.setEvents(events);
        }
        // Resets the mode based on user selection
        if(requestCode == 4 && resultCode == RESULT_OK){
            mode = sharedPreferences.getString("mode", "");
            greetingStr = "Greetings, " + generateName(mode) +". \nWho did you forget today?";
            greetingView.setText(greetingStr);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(){
        CharSequence name = "HBAReminderChannel";
        String description = "Channel for birthday reminders";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("notifyBirthday", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }
}