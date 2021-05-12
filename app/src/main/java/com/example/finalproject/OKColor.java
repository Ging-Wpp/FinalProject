package com.example.finalproject;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class OKColor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_k_color);

//        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
////        imageView.getLayoutParams().height = 800;
////        imageView.getLayoutParams().width = 600;
//        Bundle extras = getIntent().getExtras();
//        if(extras != null)
//        {
//            int img = extras.getInt("data");
//            imageView.setImageResource(img);
//        }
////        Bitmap bmp = (Bitmap) extras.getParcelable("data");
////        imageView.setImageBitmap(bmp);
////        Uri myUri = Uri.parse(extras.getString("imageUri"));
////        imageView.setImageURI(myUri);

        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.getLayoutParams().height = 800;
        imageView.getLayoutParams().width = 600;
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = (Bitmap) extras.getParcelable("data");
        imageView.setImageBitmap(bmp);

        Uri myUri = Uri.parse(extras.getString("imageUri"));
        imageView.setImageURI(myUri);

        
    }
}