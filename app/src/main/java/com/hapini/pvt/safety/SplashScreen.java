package com.hapini.pvt.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    public static final String SHARED_DATA_DETAILS = "personal_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_DATA_DETAILS,MODE_PRIVATE);
        String name = sharedPreferences.getString("name","Name");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(name.equals("Name")){
                    Intent i = new Intent(SplashScreen.this, SignUp.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }


            }
        },SPLASH_TIME_OUT);
    }
}