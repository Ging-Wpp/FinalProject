package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpage);

        new Handler(Looper.myLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, SliderActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
