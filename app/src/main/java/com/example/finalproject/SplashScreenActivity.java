package com.example.finalproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import gr.net.maroulis.library.EasySplashScreen;
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasySplashScreen easy = new EasySplashScreen(SplashScreenActivity.this)
                .withFullScreen()
                .withTargetActivity(SliderActivity.class)
                .withSplashTimeOut(3000)
                .withBackgroundResource(R.drawable.fpage);
        View easySplashScreen = easy.create();
        setContentView(easySplashScreen);
    }}