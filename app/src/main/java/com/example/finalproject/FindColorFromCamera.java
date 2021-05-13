package com.example.finalproject;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.StringTokenizer;

public class FindColorFromCamera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "FindColorFromCamera";

    JavaCameraView javaCameraView;
    Mat mat1, mat2, mRgba;

    private Scalar mBlobColorRgba;

    Scalar scalarLow, scalarHigh;
    Scalar redLow, redHigh, greenLow, greenHigh, blackLow, blackHigh;

    private Scalar CONTOUR_COLOR;
    private Mat mSpectrum;

    private ColorBlobDetector mDetector;

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

        scalarLow = new Scalar(45,100,100);
        scalarHigh = new Scalar(75,255,255);

//        blackLow = new Scalar(0,0,0);
//        blackHigh = new Scalar(180,150,50);

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
        mRgba = new Mat(height, width, CvType.CV_8UC3);
        CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mDetector.process(inputFrame.rgba());

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        List<MatOfPoint> contours = mDetector.getContours();

        Imgproc.cvtColor(mRgba, mat1, Imgproc.COLOR_RGB2HSV);
        Core.inRange(mat1, scalarLow, scalarHigh, mat2);

        //For each contour found
        for (int i = 0; i < contours.size(); i++) {

//            Imgproc.cvtColor(mRgba, mat1, Imgproc.COLOR_BGR2HSV);
//            Core.inRange(mat1, scalarLow, scalarHigh, mat2);

//            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
//            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
//            colorLabel.setTo(mBlobColorRgba);
//            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
//            mSpectrum.copyTo(spectrumLabel);

            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);

            // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
            //Imgproc.rectangle(inputFrame.rgba(), new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), (255, 0, 0, 255), 3);
            Imgproc.rectangle(mat2, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(255, 0, 0, 255));
        }
        return mat2;
    }

}
