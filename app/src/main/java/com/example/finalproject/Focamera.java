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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Focamera extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focamera);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.getLayoutParams().height = 800;
        imageView.getLayoutParams().width = 600;
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = (Bitmap) extras.getParcelable("data");
        imageView.setImageBitmap(bmp);

        final Button camera = (Button)findViewById(R.id.again_btn);

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Focamera.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(Focamera.this,new String[] { Manifest.permission.CAMERA },0);
                }
                else {
                    Toast.makeText(Focamera.this,"Permission already granted",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 0);
                    }
                }
            }
        });

        final Button ok = (Button)findViewById(R.id.forward_btn);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(Focamera.this, MainActivity.class);
                startActivity(intent1);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Focamera.this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Focamera.this,"Camera Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Focamera.this,"Storage Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Focamera.this,"Storage Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 0 && resultCode == FindObj.RESULT_OK) {
            try {
                Intent intent = new Intent(Focamera.this,Focamera.class);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                intent.putExtra("data", photo);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Camera Activity");
            }
        }

        if (requestCode == 1 && resultCode == FindObj.RESULT_OK && data!=null) {
            try {
                Uri uri = data.getData();
                Intent intent = new Intent(Focamera.this,Fogallery.class);
                intent.putExtra("imageUri", uri.toString());
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Gallery Activity");
            }
        }
    }//end onActivityResult
}