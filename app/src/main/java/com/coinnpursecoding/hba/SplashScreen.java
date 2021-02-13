package com.coinnpursecoding.hba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    ImageView confettiDrawing;
    ImageView drawingView;
    TextView titleView;

    public void hideTitle(){
        drawingView.animate().alpha(0).setDuration(500);
        titleView.animate().alpha(0).setDuration(500);
        //confettiDrawing.animate().alpha(0).setDuration(1000).setStartDelay(1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        confettiDrawing = (ImageView) findViewById(R.id.confettiView);
        drawingView = (ImageView) findViewById(R.id.drawingView);
        titleView = (TextView) findViewById(R.id.titleView);

        confettiDrawing.setTranslationY(-1000);
        confettiDrawing.animate().translationYBy(900).setDuration(1500);

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                i.putExtra("id", "1");
                startActivity(i);
                finish();
                hideTitle();
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        }, 2000);





    }
}