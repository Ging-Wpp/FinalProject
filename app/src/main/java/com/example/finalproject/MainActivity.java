package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button objBtn = findViewById(R.id.fobj_btn);
        final Button colorBtn = findViewById(R.id.fcolor_btn);

        objBtn.getBackground().setAlpha(150);
        colorBtn.getBackground().setAlpha(150);

        objBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[] { Manifest.permission.CAMERA },0);
            }
            Intent intent1 = new Intent(MainActivity.this, DetectObjByCam.class);
            startActivity(intent1);
        });

        colorBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[] { Manifest.permission.CAMERA },0);
            }
            Intent intent1 = new Intent(MainActivity.this, FindColor.class);
            startActivity(intent1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 0 && resultCode == MainActivity.RESULT_OK) {
            try {
                Intent intent = new Intent(MainActivity.this,FindColor.class);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                intent.putExtra("data", photo);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Camera Activity");
            }
        }
    }

}