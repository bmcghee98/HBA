package com.coinnpursecoding.hba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getStringExtra("name");

        String GROUP_CODE = "com.coinnpursecoding.hba";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyBirthday")
                .setSmallIcon(R.drawable.cake_icon2)
                .setContentTitle("Honest Birthday App")
                .setContentText(generatePhrase(name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle());


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Just a random number so they stack (.setGroup isn't working the way I want it to)
        int requestCode = MainActivity.sharedPreferences.getInt("notifCode", 1);
        Log.i("Initial request2", Integer.toString(requestCode));
        requestCode = ++requestCode;
        notificationManager.notify(requestCode, builder.build());
        MainActivity.sharedPreferences.edit().putInt("notifCode", requestCode).apply();

        Log.i("Final request2", Integer.toString(MainActivity.sharedPreferences.getInt("notifCode", requestCode)));
    }


    public String generatePhrase(String name){
        Random rand = new Random();
        int num = rand.nextInt(15);
        String phrase = "";

        switch (num){
            case 0:
                phrase = "Hey " + MainActivity.generateName(MainActivity.mode) + ", don't you forget " + StringUtils.capitalize(name) + "'s birthday today!";
                break;
            case 1:
                phrase = "Did you forget? " + StringUtils.capitalize(name) + " has a birthday today!";
                break;
            case 2:
                phrase = "I hope you didn't forget about " + StringUtils.capitalize(name) + "'s birthday today...";
                break;
            case 3:
                phrase = "Friendly reminder that " +  StringUtils.capitalize(name) + "'s birthday is today.";
                break;
            case 4:
                phrase = "Hello? Did you forget " + StringUtils.capitalize(name) + "'s birthday today? Don't make me come over there!";
                break;
            case 5:
                phrase = StringUtils.capitalize(MainActivity.generateName(MainActivity.mode)) + ", " + StringUtils.capitalize(name) + "'s birthday is today.";
                break;
            case 6:
                phrase = "Remember your friend " + StringUtils.capitalize(name) + "? Yeah, their birthday is today! Buy a present or else... :)";
                break;
            case 7:
                phrase = "You don't want your friends to hate you, do you? " + StringUtils.capitalize(name) + "'s birthday is today. Say something! Or I'll hate you too >:(";
                break;
            case 8:
                phrase = "It's that time of the month again. " + StringUtils.capitalize(name) + " has a birthday today!";
                break;
            case 9:
                phrase = "Hope you got a present, " + MainActivity.generateName(MainActivity.mode) + ", because " + StringUtils.capitalize(name) + "'s birthday is today.";
                break;
            case 10:
                phrase = "I'll never get sick of reminding you about birthdays ... not even " + StringUtils.capitalize(name) + "'s ... which is today.";
                break;
            case 11:
                phrase = "I know where you live! And I'll know if you've forgotten " + StringUtils.capitalize(name) + "'s birthday today!";
                break;
            case 12:
                phrase = "Another one? Yes, " + MainActivity.generateName(MainActivity.mode) + ". " + StringUtils.capitalize(name) + "'s birthday is today.";
                break;
            case 13:
                phrase = "What's up, " + MainActivity.generateName(MainActivity.mode) + "? " + StringUtils.capitalize(name) + "'s birthday is today.";
                break;
            case 14:
                phrase = "Take some time to wish " + StringUtils.capitalize(name) + " a happy birthday!";
                break;
            default:
                break;
        }

        return phrase;
    }
}

