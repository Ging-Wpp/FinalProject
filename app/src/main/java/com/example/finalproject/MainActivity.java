package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button fobj = findViewById(R.id.fobj_btn);
        final Button fcolor = findViewById(R.id.fcolor_btn);

        fobj.getBackground().setAlpha(150);
        fcolor.getBackground().setAlpha(150);

//        fobj.setOnClickListener(v -> {
//            Intent intent1 = new Intent(MainActivity.this, detectobjbycam.class);
//            startActivity(intent1);
//        });

        fobj.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[] { Manifest.permission.CAMERA },0);
            }
//                else {
//                    Toast.makeText(MainActivity.this,"Permission already granted",Toast.LENGTH_SHORT).show();
////                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                    if (intent.resolveActivity(getPackageManager()) != null) {
////                        startActivityForResult(intent, 0);
////                    }
//                }
            Intent intent1 = new Intent(MainActivity.this, detectobjbycam.class);
            startActivity(intent1);
        });

//        fcolor.setOnClickListener(v -> {
//            Intent intent2 = new Intent(MainActivity.this, detectColor.class);
//            startActivity(intent2);
//        });

        fcolor.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[] { Manifest.permission.CAMERA },0);
            }
//                else {
//                    Toast.makeText(MainActivity.this,"Permission already granted",Toast.LENGTH_SHORT).show();
////                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                    if (intent.resolveActivity(getPackageManager()) != null) {
////                        startActivityForResult(intent, 0);
////                    }
//                }
            Intent intent1 = new Intent(MainActivity.this, detectColor.class);
            startActivity(intent1);
        });
    }//end onCreate()

//    @Override
//    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
//    {
//        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(MainActivity.this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(MainActivity.this,"Camera Permission Denied",Toast.LENGTH_SHORT).show();
//            }
//        }
//        else if (requestCode == STORAGE_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(MainActivity.this,"Storage Permission Granted",Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(MainActivity.this,"Storage Permission Denied",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }//end onRequestPermissionsResult
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 0 && resultCode == MainActivity.RESULT_OK) {
            try {
                Intent intent = new Intent(MainActivity.this,detectColor.class);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                intent.putExtra("data", photo);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Camera Activity");
            }
        }

//        if (requestCode == 1 && resultCode == MainActivity.RESULT_OK && data!=null) {
//            try {
//                Uri uri = data.getData();
//                Intent intent = new Intent(MainActivity.this,Fcgallery.class);
//                intent.putExtra("imageUri", uri.toString());
//                startActivity(intent);
//            } catch (Exception e) {
//                Log.e("Log", "Error from Gallery Activity");
//            }
//        }
    }//end onActivityResult
}//end class MainActivity