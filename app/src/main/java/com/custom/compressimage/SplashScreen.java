package com.custom.compressimage;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    protected static final int SPLASH_SCREEN_TIME_OUT = 2500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.

        setContentView(R.layout.splash_screen_activity);
        //this will bind your MainActivity.class file with activity_main.

        new Handler().postDelayed(() -> {
            Intent i=new Intent(SplashScreen.this, HomeScreen.class);
            //Intent is used to switch from one activity to another.

            startActivity(i);
            //invoke the SecondActivity.

            finish();
            //the current activity will get finished.
        }, SPLASH_SCREEN_TIME_OUT);
    }
}

