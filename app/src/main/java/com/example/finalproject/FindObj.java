package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FindObj extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_obj);

        final Button storage = (Button)findViewById(R.id.fcolor_btn);
        final Button camera = (Button)findViewById(R.id.fobj_btn);

//        camera.setOnClickListener(v -> {
//            Intent intent1 = new Intent(FindObj.this, FindColorFromCamera.class);
//            startActivity(intent1);
//        });

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(FindObj.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(FindObj.this,new String[] { Manifest.permission.CAMERA },0);
                }
                else {
                    Toast.makeText(FindObj.this,"Permission already granted",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(intent, 0);
//                    }
                }

                Intent intent1 = new Intent(FindObj.this, detectobjbycam.class);
                startActivity(intent1);

            }
        });

        storage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(FindObj.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(FindObj.this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },1);
                }
                else {
                    Toast.makeText(FindObj.this,"Permission already granted",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Select photo from"), 1);
                    }
                }
            }
        });

    }//end onCreate()

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FindObj.this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(FindObj.this,"Camera Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FindObj.this,"Storage Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(FindObj.this,"Storage Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 0 && resultCode == FindObj.RESULT_OK) {
            try {
                Intent intent = new Intent(FindObj.this,Focamera.class);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                intent.putExtra("data", photo);
                startActivity(intent);
//                Intent intent1 = new Intent(FindObj.this, detectColor.class);
//                startActivity(intent1);
            } catch (Exception e) {
                Log.e("Log", "Error from Camera Activity");
            }
        }

        if (requestCode == 1 && resultCode == FindObj.RESULT_OK && data!=null) {
            try {
                Uri uri = data.getData();
                Intent intent = new Intent(FindObj.this,Fogallery.class);
                intent.putExtra("imageUri", uri.toString());
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Gallery Activity");
            }
        }
    }//end onActivityResult
}//end class MainActivity