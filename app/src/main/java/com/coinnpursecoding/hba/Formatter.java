package com.coinnpursecoding.hba;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Formatter {

    // Date converting made easy
    public static String formatLongDate(long longDate, String format){
        Date date = new Date(longDate);

        SimpleDateFormat simpleDateFormat;

        switch (format){
            case "MM/dd/yyyy":
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                break;
            case "MMMM d, yyyy":
                simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                break;
            case "MMM d":
                simpleDateFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
                break;
            case "yyyyy.MMMMM.dd GGG hh:mm aaa":
                simpleDateFormat = new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa", Locale.getDefault());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + format);
        }

        return simpleDateFormat.format(date);
    }

    // Converts a Date to a long value
    public static long dateToLong(String date, int year){
        SimpleDateFormat f = new SimpleDateFormat("MMM d", Locale.getDefault());

        // Creates the Date
        Date d = new Date();
        try {
            d = f.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert d != null;
        Log.i("convertedDate", formatLongDate(d.getTime(), "MM/dd/yyyy"));

        // Once converted, adds the appropriate year (default is 1970)
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setTime(d);
        c.add(Calendar.YEAR, year - 1970);

        return c.getTimeInMillis();
    }

    // Returns the month shown at beginning of string
    public static String getFirstWord(String any){
        return any.substring(0, any.indexOf(' '));
    }

    // Converts date string to array, then lets user choose day/month/year
    public static String getDateValue(String date, int index){
        String[] str = date.split("/");
        return str[index];

    }
}
