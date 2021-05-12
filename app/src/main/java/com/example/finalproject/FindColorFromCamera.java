package com.example.finalproject;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.StringTokenizer;

public class FindColorFromCamera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "FindColorFromCamera";

    JavaCameraView javaCameraView;
    Mat mat1, mat2;

    private Scalar mBlobColorRgba;

    Scalar scalarLow, scalarHigh;
    Scalar redLow, redHigh, greenLow, greenHigh, blackLow, blackHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findcolorfromcamera);

        OpenCVLoader.initDebug();

        javaCameraView = findViewById(R.id.color_blob_detection_activity_surface_view);
        javaCameraView.setCameraIndex(0);

        String rgb = getIntent().getStringExtra("text");
        String[] strArray = rgb.split(", ");
        int[] intArray = new int[strArray.length];
        for(int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]);
        }
        int r = intArray[0];
        int g = intArray[1];
        int b = intArray[2];
        Log.d(TAG,"rgb: " + rgb);
        Log.d(TAG,"r: " + intArray[0]);
        Log.d(TAG,"g: " + intArray[1]);
        Log.d(TAG,"b: " + intArray[2]);

        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);

        int h = (int) hsv[0];
        float sc = hsv[1]*100f;
        int s = (int) sc;
        float vc = hsv[2]*100f;
        int v = (int) vc;
        Log.d(TAG,"h: " + h);
        Log.d(TAG,"s: " + s);
        Log.d(TAG,"v: " + v);

        //set sentity r= low+15, high+15
        //detect green color in hsv

        scalarLow = new Scalar(10,100,20);
        scalarHigh = new Scalar(20,255,200);

        blackLow = new Scalar(0,0,0);
        blackHigh = new Scalar(180,150,50);

//        Core.inRange(hsv, new Scalar());


        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        javaCameraView.enableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        javaCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat1 = new Mat(width, height, CvType.CV_8UC3);
        mat2 = new Mat(width, height, CvType.CV_8UC3);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Imgproc.cvtColor(inputFrame.rgba(), mat1, Imgproc.COLOR_BGR2HSV);
        Core.inRange(mat1, scalarLow, scalarHigh, mat2);
        return mat2;
    }
}
