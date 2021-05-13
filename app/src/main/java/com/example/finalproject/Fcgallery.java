package com.example.finalproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class Fcgallery extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    ImageView ImageView;
    TextView ResultTv;
    TextView HexName;
    TextView Name;
    View ColorView;
    Bitmap bitmap;
    private ClipData clipData;
    private ClipboardManager clipboardManager;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "DefaultLocale", "QueryPermissionsNeeded"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcgallery);

        ImageView = (ImageView) findViewById(R.id.imageView);
        ResultTv = findViewById(R.id.resultTv);
        ColorView = findViewById(R.id.colorView);
        HexName = findViewById(R.id.hex);
        Name = findViewById(R.id.name);

        ImageView.setDrawingCacheEnabled(true);
        ImageView.buildDrawingCache(true);

        final Button camera = findViewById(R.id.camera);
        camera.setOnClickListener(v -> {
            Intent intent1 = new Intent(Fcgallery.this, detectColor.class);
            startActivity(intent1);
        });

        final Button gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(v -> {
            Intent intent1 = new Intent(Fcgallery.this, Fcgallery.class);
            startActivity(intent1);
        });

        ImageView.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ){
                bitmap = ImageView.getDrawingCache();

                int pixel = bitmap.getPixel((int)event.getX(), (int)event.getY());

                //getting RGB values
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                //getting HEX values
                ArrayList<String> hexArr = new ArrayList<>();
                String hex = Integer.toHexString(pixel);
                String hex2 = "#" + hex.substring(2);
                hexArr.add(hex2);

                //set RGB, HEX values to textView
                String[] colorNames = getResources().getStringArray(R.array.colorNames);
                String[] colorCodes = getResources().getStringArray(R.array.codecolors);
                ResultTv.setText(String.format("RGB: %d, %d, %d", r, g, b));
                HexName.setText("\nHEX: " + hex2.toUpperCase());

//                    StringBuffer sb = new StringBuffer();
//                    for(int i = 0; i < colorCodes.length; i++) {
//                        sb.append(colorCodes[i]);
//                    }
//                    String str = sb.toString();
//
//                    ArrayList<String> list = new ArrayList<>(Arrays.asList(str.split("#")));

//                    ResultTv.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex2);



//                for (String s : colorCodes)
//                {
////                        ResultTv.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex2);
//                    for(int i=0;i<colorCodes.length;i++) {
//                        if(hexArr.get(0).equalsIgnoreCase(colorCodes[i])) {
//                            ResultTv.setText("RGB: " + r + ", " + g + ", " + b);
//                            HexName.setText("\nHEX: " + hex2);
//                            Name.setText("\nColor name: " + colorNames[i]);
//                        }else{
//                            i++;
//                        }
//                        i++;
//                    }
//                }

//                    for(int i=0;i<colorCodes.length;i++) {
//                        if(hexArr.get(0).equalsIgnoreCase(colorCodes[i])) {
//                            mResultTv.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex2 +
//                                    "\ncolor name: " + colorNames[i]);
//                        }
//                        i++;
//                    }

                //set background of view according to the picked color
                ColorView.setBackgroundColor(Color.rgb(r,g,b));

            }
            return true;
        });

//        ImageView.getLayoutParams().height = 800;
//        ImageView.getLayoutParams().width = 600;
        Bundle extras = getIntent().getExtras();
        Uri myUri = Uri.parse(extras.getString("imageUri"));
        ImageView.setImageURI(myUri);

//        final Button gallery = (Button)findViewById(R.id.again_btn);
//
//        gallery.setOnClickListener(v -> {
//            if (ContextCompat.checkSelfPermission(Fcgallery.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//                ActivityCompat.requestPermissions(Fcgallery.this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },1);
//            }
//            else {
//                Toast.makeText(Fcgallery.this,"Permission already granted",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(Intent.createChooser(intent, "Select photo from"), 1);
//                }
//            }
//        });

//        final Button ok = (Button)findViewById(R.id.forward_btn);
//        ok.setOnClickListener(v -> {
//            Intent intent1 = new Intent(Fcgallery.this, MainActivity.class);
//            startActivity(intent1);
//        });

        final Button copyText = (Button) findViewById(R.id.copy);
        @SuppressLint("CutPasteId") TextView hexcode = (TextView)findViewById(R.id.hex);
        clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        copyText.setOnClickListener(v -> {
            String txtcopy = hexcode.getText().toString();
            String copy = txtcopy.substring(6);
            clipData = ClipData.newPlainText("text",copy);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getApplicationContext(),copy, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(Fcgallery.this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(Fcgallery.this,"Camera Permission Denied",Toast.LENGTH_SHORT).show();
//            }
//        }
//        else
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Fcgallery.this,"Storage Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Fcgallery.this,"Storage Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

//        if (requestCode == 0 && resultCode == FindObj.RESULT_OK) {
//            try {
//                Intent intent = new Intent(Fcgallery.this,Fccamera.class);
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                intent.putExtra("data", photo);
//                startActivity(intent);
//            } catch (Exception e) {
//                Log.e("Log", "Error from Camera Activity");
//            }
//        }

        if (requestCode == 1 && resultCode == FindColor.RESULT_OK && data!=null) {
            try {
                Uri uri = data.getData();
                Intent intent = new Intent(Fcgallery.this,Fcgallery.class);
                intent.putExtra("imageUri", uri.toString());
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Gallery Activity");
            }
        }
    }//end onActivityResult
}