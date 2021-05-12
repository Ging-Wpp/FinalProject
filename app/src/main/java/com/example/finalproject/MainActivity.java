package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button fobj = findViewById(R.id.fobj_btn);
        final Button fcolor = findViewById(R.id.fcolor_btn);

        fobj.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, FindObj.class);
            startActivity(intent1);
        });

        fcolor.setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this, FindColor.class);
            startActivity(intent2);
        });
    }//end onCreate()
}//end class MainActivity