//package com.example.finalproject;
//
//import android.Manifest;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import java.util.ArrayList;
//
//public class Fccamera extends AppCompatActivity {
//
//    private static final int CAMERA_PERMISSION_CODE = 100;
//    private static final int STORAGE_PERMISSION_CODE = 200;
//    ImageView ImageView;
//    TextView ResultTv;
//    TextView HexName;
//    TextView Name;
//    View ColorView;
//    Bitmap bitmap;
//    private ClipData clipData;
//    private ClipboardManager clipboardManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fccamera);
//
//        ImageView = (ImageView) findViewById(R.id.imageView);
//        ResultTv = findViewById(R.id.resultTv);
//        ColorView = findViewById(R.id.colorView);
//        HexName = findViewById(R.id.hex);
//        Name = findViewById(R.id.name);
//
//        ImageView.setDrawingCacheEnabled(true);
//        ImageView.buildDrawingCache(true);
//
//        ImageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE ){
//                    bitmap = ImageView.getDrawingCache();
//
//                    int pixel = bitmap.getPixel((int)event.getX(), (int)event.getY());
//
//                    //getting RGB values
//                    int r = Color.red(pixel);
//                    int g = Color.green(pixel);
//                    int b = Color.blue(pixel);
//
//                    //getting HEX values
//                    ArrayList<String> hexArr = new ArrayList<String>();
//                    String hex = Integer.toHexString(pixel);
//                    String hex2 = "#"+hex.substring(2);
//                    hexArr.add(hex2);
//
//                    //set RGB, HEX values to textView
//                    String[] colorNames = getResources().getStringArray(R.array.colorNames);
//                    String[] colorCodes = getResources().getStringArray(R.array.codecolors);
//                    ResultTv.setText("RGB: " + r + ", " + g + ", " + b);
//                    HexName.setText("\nHEX: " + hex2);
////
////                    StringBuffer sb = new StringBuffer();
////                    for(int i = 0; i < colorCodes.length; i++) {
////                        sb.append(colorCodes[i]);
////                    }
////                    String str = sb.toString();
////
////                    ArrayList<String> list = new ArrayList<>(Arrays.asList(str.split("#")));
//
////                    for (String s : colorCodes)
////                    {
//////                        ResultTv.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex2);
////                        for(int i=0;i<colorCodes.length;i++) {
////                            if(hexArr.get(0).equalsIgnoreCase(colorCodes[i])) {
////                                ResultTv.setText("RGB: " + r + ", " + g + ", " + b);
////                                HexName.setText("\nHEX: " + hex2);
////                                Name.setText("\nColor name: " + colorNames[i]);
////                            }else{
////                                i++;
////                            }
////                            i++;
////                        }
////                    }
////
////                    for (String s : colorCodes)
////                    {
//////                        mResultTv.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex2);
////                        for(int i=0;i<colorCodes.length;i++) {
////                            if(hexArr.get(0).equalsIgnoreCase(colorCodes[i])) {
////                                ResultTv.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex2 +
////                                        "\nColor name: " + colorNames[i]);
////                            }else{
////                                i++;
////                            }
////                            i++;
////                        }
////                    }
//
////                    for(int i=0;i<colorCodes.length;i++) {
////                        if(hexArr.get(0).equalsIgnoreCase(colorCodes[i])) {
////                            mResultTv.setText("RGB: " + r + ", " + g + ", " + b + "\nHEX: " + hex2 +
////                                    "\ncolor name: " + colorNames[i]);
////                        }
////                        i++;
////                    }
//
//                    //set background of view according to the picked color
//                    ColorView.setBackgroundColor(Color.rgb(r,g,b));
//
//                }
//                return true;
//            }
//        });
//
//
////        ImageView.getLayoutParams().height = 800;
////        ImageView.getLayoutParams().width = 600;
//        Bundle extras = getIntent().getExtras();
//        Bitmap bmp = (Bitmap) extras.getParcelable("data");
//        ImageView.setImageBitmap(bmp);
//
//        final Button camera = (Button)findViewById(R.id.again_btn);
//        camera.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(Fccamera.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
//                    ActivityCompat.requestPermissions(Fccamera.this,new String[] { Manifest.permission.CAMERA },0);
//                }
//                else {
//                    Toast.makeText(Fccamera.this,"Permission already granted",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(intent, 0);
//                    }
//                }
//            }
//        });
//
//        final Button ok = (Button)findViewById(R.id.forward_btn);
//        ok.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent1 = new Intent(Fccamera.this, MainActivity.class);
//                startActivity(intent1);
//            }
//        });
//
//        final Button copyText = (Button) findViewById(R.id.copy);
//        TextView hexcode = (TextView)findViewById(R.id.hex);
//        clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
//        copyText.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String txtcopy = hexcode.getText().toString();
//                String copy = txtcopy.substring(6);
//                clipData = ClipData.newPlainText("text",copy);
//                clipboardManager.setPrimaryClip(clipData);
//                Toast.makeText(getApplicationContext(),"Data Copied to Clipboard", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
//    {
//        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(Fccamera.this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(Fccamera.this,"Camera Permission Denied",Toast.LENGTH_SHORT).show();
//            }
//        }
//        else if (requestCode == STORAGE_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(Fccamera.this,"Storage Permission Granted",Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(Fccamera.this,"Storage Permission Denied",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode,resultCode,data);
//
//        if (requestCode == 0 && resultCode == Fccamera.RESULT_OK) {
//            try {
//                Intent intent = new Intent(Fccamera.this,Fccamera.class);
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                intent.putExtra("data", photo);
//                startActivity(intent);
//            } catch (Exception e) {
//                Log.e("Log", "Error from Camera Activity");
//            }
//        }
//
////        if (requestCode == 1 && resultCode == Fccamera.RESULT_OK && data!=null) {
////            try {
////                Uri uri = data.getData();
////                Intent intent = new Intent(Fccamera.this,OKColor.class);
////                intent.putExtra("imageUri", uri.toString());
////                startActivity(intent);
////            } catch (Exception e) {
////                Log.e("Log", "Error from Gallery Activity");
////            }
////        }
//    }//end onActivityResult
//}