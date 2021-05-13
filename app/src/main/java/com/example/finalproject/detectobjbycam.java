package com.example.finalproject;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class detectobjbycam extends AppCompatActivity implements OnTouchListener, CvCameraViewListener2 {
    private static final String TAG = "detectobjbycam";

    private boolean mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR; //Scalar กำหนดเป็นพื้นที่สีอื่น

    private TextView mResultTv;
    private TextView rgb;
    private TextView HexCode;
    private TextView Name;
    private Button colorBtn;
    Bitmap bitmap;
    private ClipData clipData;
    private ClipboardManager clipboardManager;
    private CameraBridgeViewBase mOpenCvCameraView;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(detectobjbycam.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public detectobjbycam() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_detectobjbycam);

        mOpenCvCameraView = findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
//        mOpenCvCameraView.setCameraDisplayOrientation(this);

        mOpenCvCameraView.setDrawingCacheEnabled(true);
        mOpenCvCameraView.buildDrawingCache(true);

        rgb = findViewById(R.id.resultTv);
        HexCode = findViewById(R.id.hex);
        Name = findViewById(R.id.name);

        final Button camera = findViewById(R.id.camera);
        final Button gallery = findViewById(R.id.gallery);

        camera.setOnClickListener(v -> {
            Intent intent1 = new Intent(detectobjbycam.this, detectobjbycam.class);
            startActivity(intent1);
        });

//        gallery.setOnClickListener(v -> {
//            Intent intent2 = new Intent(detectobjbycam.this, Fogallery.class);
//            startActivity(intent2);
//        });

        gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(detectobjbycam.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(detectobjbycam.this,new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },1);
                }
                else {
                    Toast.makeText(detectobjbycam.this,"Permission already granted",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Select photo from"), 1);
                    }
                }
            }
        });

//        mResultTv = findViewById(R.id.resultTv);
    }//end oncreate

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(detectobjbycam.this,"Camera Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(detectobjbycam.this,"Camera Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(detectobjbycam.this,"Storage Permission Granted",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(detectobjbycam.this,"Storage Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 0 && resultCode == FindObj.RESULT_OK) {
            try {
                Intent intent = new Intent(detectobjbycam.this,detectColor.class);
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
                Intent intent = new Intent(detectobjbycam.this,Fogallery.class);
                intent.putExtra("imageUri", uri.toString());
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Gallery Activity");
            }
        }
    }//end onActivityResult

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    // When a motion event happens (someone touches the device)
    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols(); //get resolution of display
        int rows = mRgba.rows(); //get resolution of display

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2; //get resolution of display
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2; //get resolution of display

        int x = (int) event.getX() - xOffset;
        int y = (int) event.getY() - yOffset;

        //The place where the screen was touched
        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        //Ensure it is a multiple of 4
        touchedRect.x = (x > 4) ? x - 4 : 0;
        touchedRect.y = (y > 4) ? y - 4 : 0;

        // If  x+4 < cols then ?"" else :""
        touchedRect.width = (x + 4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y + 4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;

        //create a touched regionmat from the image created from the touches
        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        //Convert the new mat to HSV colour space
        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        //converts scalar to hsv to RGB
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        mDetector.setHsvColor(mBlobColorHsv);

        // Resize the image to specture size
        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);

        mIsColorSelected = true;

        // Release all mats
        touchedRegionRgba.release();
        touchedRegionHsv.release();

        String hex = String.format("#%02x%02x%02x", (int)mBlobColorRgba.val[0], (int)mBlobColorRgba.val[1], (int)mBlobColorRgba.val[2]);
//        mResultTv.setText("RGB: " + (int)mBlobColorRgba.val[0] + ", " + (int)mBlobColorRgba.val[1] + ", " + (int)mBlobColorRgba.val[2] + "\nHex Code: " + hex.toUpperCase());
//


        rgb.setText("RGB: " + (int)mBlobColorRgba.val[0] + ", " + (int)mBlobColorRgba.val[1] + ", " + (int)mBlobColorRgba.val[2]);
        HexCode.setText("Hex Code: " + hex.toUpperCase());
//        Name.setText("\nColor name: " + colorNames[i]);

        final Button copyText = (Button) findViewById(R.id.copy);
        TextView rgb = (TextView)findViewById(R.id.resultTv);
        clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        copyText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String txtcopy = rgb.getText().toString();
                String copy = txtcopy.substring(5);
                clipData = ClipData.newPlainText("text",copy);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(),copy, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(detectobjbycam.this,FindColorFromCamera.class);
//                intent.putExtra("text", copy);
//                startActivity(intent);
            }
        });



//        final Button copyText = (Button) findViewById(R.id.copy);
//        TextView hexcode = (TextView)findViewById(R.id.hex);
//        clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
//        copyText.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String txtcopy = hexcode.getText().toString();
//                String copy = txtcopy.substring(10);
//                clipData = ClipData.newPlainText("text",copy);
//                clipboardManager.setPrimaryClip(clipData);
//                Toast.makeText(getApplicationContext(),"Data Copied to Clipboard", Toast.LENGTH_SHORT).show();
//            }
//        });

//        Mat colorLabel = mRgba.submat(20, 68, 20, 278);
//        colorLabel.setTo(mBlobColorRgba);

        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) { //colors in camera frame
        mRgba = inputFrame.rgba();

        if (mIsColorSelected) { //if selected new color then re process again
            mDetector.process(mRgba);

            List<MatOfPoint> contours = mDetector.getContours();
            Log.e(TAG, "Contours count: " + contours.size());

            MatOfPoint2f approxCurve = new MatOfPoint2f();
            //For each contour found
            for (int i=0; i<contours.size(); i++) {

//                Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
                Mat colorLabel = mRgba.submat(20, 68, 20, 278);
                colorLabel.setTo(mBlobColorRgba);
//                Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
//                mSpectrum.copyTo(spectrumLabel);

                //Convert contours(i) from MatOfPoint to MatOfPoint2f
                MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
                //Processing on mMOP2f1 which is in type MatOfPoint2f
                double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

                //Convert back to MatOfPoint
                MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

                // Get bounding rect of contour
                Rect rect = Imgproc.boundingRect(points);

                // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
//                Core.rectangle(contoursFrame, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), (255, 0, 0, 255), 3);
                Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0, 255, 0));
            }
        }
        return mRgba;
    }

    //final conversion
    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

}
