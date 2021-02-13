package com.coinnpursecoding.hba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.applandeo.materialcalendarview.EventDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import static com.coinnpursecoding.hba.ViewActivity.birthdays;



public class mySQLiteDBHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String TABLE_NAME = "birthday_table";
    public static final String COL1 = "name";
    public static final String COL2 = "longdate";
    private static final String COL3 = "date";


    public mySQLiteDBHandler(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL1 + " VARCHAR, " + COL2 +
                " INTEGER, " + COL3 + " VARCHAR)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /* POSSIBLY SEVERELY OVER-ENGINEERED CODE BELOW */

    public boolean addData(Birthday person) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, person.getName());
        contentValues.put(COL2, person.getLongDate());
        contentValues.put(COL3, person.getDbDate());

        Log.d(TAG, "Adding: " + person.getName() + ", " + person.getLongDate() + ", " + person.getDbDate());
        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public int getDatabaseSize(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        Log.i("# of data entries", Integer.toString(data.getCount()));

        data.close();

        return data.getCount();
    }


    public void getLongData(List<EventDay> entered) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL2 + " FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        if (data.getColumnCount() != 0) {
            data.moveToFirst();

            int i = 0;
            do {
                Date n = new Date(data.getLong(data.getColumnIndex(COL2)));
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTime(n);

                entered.add(new EventDay(calendar, R.drawable.cake_icon2));

                i++;
            } while (data.moveToNext());

            data.close();
        }
    }

    public void fillBirthdayList(String month){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL2 + " ASC";
        Cursor data = db.rawQuery(query, null);

        // If database isn't empty and array isn't full
        if (data.getColumnCount() != 0 && birthdays.size() != data.getColumnCount()) {
            data.moveToFirst();

            int i = 0;
            do {
                // Gets the MM/dd/yyyy string from database
                String comparedDate = data.getString(data.getColumnIndex(COL3));

                // If month and year are the same, add to array
                if(Formatter.getDateValue(comparedDate, 0).equals(month)) {

                    // Formats for the View activity ex. Bob (Oct 20th)
                    String date = Formatter.formatLongDate(data.getLong(data.getColumnIndex(COL2)), "MMM d");
                    String entry = data.getString(data.getColumnIndex(COL1)) + " (" + date + ")";
                    birthdays.add(entry);
                }
                i++;
            } while (data.moveToNext());

            data.close();
        }
    }

    public ArrayList<String> getNameList(long date){
        ArrayList<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL2 + " ASC";
        Cursor data = db.rawQuery(query, null);

        data.moveToFirst();

        int i = 0;
        do{
            if (data.getLong(data.getColumnIndex(COL2)) == date){
                names.add(data.getString(data.getColumnIndex(COL1)));
            }
        } while (data.moveToNext());

        data.close();

        return names;
    }
    
    public boolean removeData(String name, long date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL2 + " ASC";
        Cursor data = db.rawQuery(query, null);

        boolean success = false;
        data.moveToFirst();

        int i = 0;
        do{
            if (data.getString(data.getColumnIndex(COL1)).equals(name) &&
            data.getLong(data.getColumnIndex(COL2)) == date){
                db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE "+ COL1 + "='"+
                        name +"' AND " + COL2 + "='"+ date + "'");
                success = true;
            }
        } while (data.moveToNext());

        data.close();
        return success;
    }

    public void updateBirthdays(String year){

    }
}