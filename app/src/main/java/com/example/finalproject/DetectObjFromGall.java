package com.example.finalproject;

import java.util.List;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import www.sanju.motiontoast.MotionToast;

public class DetectObjFromGall extends AppCompatActivity implements CvCameraViewListener2 {
    
    private static final String TAG = "DetectObjFromGall";

    private Mat mRgba;
    private Scalar mBlobColorRgba, mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private int r, g, b;
    private ClipData clipData;
    private ClipboardManager clipboardManager;
    private CameraBridgeViewBase mOpenCvCameraView;
    private FloatingActionButton mAddAlarmFab, mAddPersonFab;
    private ExtendedFloatingActionButton mAddFab;
    private TextView addAlarmActionText, addPersonActionText;
    private Boolean isAllFabsVisible;

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    public DetectObjFromGall() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @SuppressLint({"QueryPermissionsNeeded", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_detect_obj_from_gall);

        String extra = getIntent().getStringExtra("text");
        String extra2 = getIntent().getStringExtra("text2");
        String extra3 = getIntent().getStringExtra("text3");

        mOpenCvCameraView = findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        TextView rgb1 = findViewById(R.id.resultTv);
        @SuppressLint("CutPasteId") TextView hexCode = findViewById(R.id.hex);
        TextView name = findViewById(R.id.name);

        rgb1.setText(String.format("RGB: %s", extra));
        hexCode.setText(String.format("\nHEX: %s", extra2));
        name.setText(extra3);

        String[] strArray = extra.split(", ");
        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]);
        }
        r = intArray[0];
        g = intArray[1];
        b = intArray[2];

        View colorView = findViewById(R.id.colorView);
        colorView.setBackgroundColor(Color.rgb(r, g, b));

        final ImageButton camera = findViewById(R.id.camera);
        final ImageButton gallery = findViewById(R.id.gallery);

        camera.setOnClickListener(v -> {
            Intent intent1 = new Intent(DetectObjFromGall.this, DetectObjByCam.class);
            startActivity(intent1);
        });

        gallery.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(DetectObjFromGall.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(DetectObjFromGall.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(Intent.createChooser(intent, "Select photo from"), 1);
            }
        });

        ImageView logo = findViewById(R.id.imageView4);
        logo.setOnClickListener(view -> {
            Intent intent = new Intent(DetectObjFromGall.this,MainActivity.class);
            startActivity(intent);
        });
        TextView find = findViewById(R.id.textView3);
        find.setOnClickListener(view -> {
            Intent intent = new Intent(DetectObjFromGall.this,MainActivity.class);
            startActivity(intent);
        });

        mAddFab = findViewById(R.id.add_fab);
        mAddAlarmFab = findViewById(R.id.add_alarm_fab);
        mAddPersonFab = findViewById(R.id.add_person_fab);
        addAlarmActionText = findViewById(R.id.add_alarm_action_text);
        addPersonActionText = findViewById(R.id.add_person_action_text);

        mAddAlarmFab.setVisibility(View.GONE);
        mAddPersonFab.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        mAddFab.shrink();

        mAddFab.setOnClickListener(view -> {
            if (!isAllFabsVisible) {
                mAddAlarmFab.show();
                mAddPersonFab.show();
                addAlarmActionText.setVisibility(View.VISIBLE);
                addPersonActionText.setVisibility(View.VISIBLE);
                mAddFab.extend();
                isAllFabsVisible = true;
            } else {
                mAddAlarmFab.hide();
                mAddPersonFab.hide();
                addAlarmActionText.setVisibility(View.GONE);
                addPersonActionText.setVisibility(View.GONE);
                mAddFab.shrink();
                isAllFabsVisible = false;
            }
        });

        @SuppressLint("CutPasteId") TextView rgb = findViewById(R.id.resultTv);
        @SuppressLint("CutPasteId") TextView hexcp = findViewById(R.id.hex);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mAddPersonFab.setOnClickListener(view -> {
            String txtcopy = rgb.getText().toString();
            String copy = txtcopy.substring(5);
            clipData = ClipData.newPlainText("text", copy);
            clipboardManager.setPrimaryClip(clipData);
            MotionToast.Companion.darkColorToast(DetectObjFromGall.this,"RGB: " + copy, MotionToast.TOAST_SUCCESS, MotionToast.GRAVITY_CENTER, MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(DetectObjFromGall.this, R.font.helvetica_regular));
        });
        mAddAlarmFab.setOnClickListener(view -> {
            String txtcopy = hexcp.getText().toString();
            String copy2 = txtcopy.substring(5);
            clipData = ClipData.newPlainText("text2", copy2);
            clipboardManager.setPrimaryClip(clipData);
            MotionToast.Companion.darkColorToast(DetectObjFromGall.this,"Hex: " + copy2.toUpperCase(), MotionToast.TOAST_SUCCESS, MotionToast.GRAVITY_CENTER, MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(DetectObjFromGall.this, R.font.helvetica_regular));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == DetectObjFromGall.RESULT_OK) {
            try {
                Intent intent = new Intent(DetectObjFromGall.this, FindColor.class);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                intent.putExtra("data", photo);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Camera Activity");
            }
        }
        if (requestCode == 1 && resultCode == DetectObjFromGall.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                Intent intent = new Intent(DetectObjFromGall.this, DetectObjFromGall.class);
                intent.putExtra("imageUri", uri.toString());
                startActivity(intent);
            } catch (Exception e) {
                Log.e("Log", "Error from Gallery Activity");
            }
        }
    }

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
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mBlobColorHsv = converScalarRgba2HSV(new Scalar(r,g,b));
        Mat mRgbaFiltered = new Mat();
        Imgproc.cvtColor(mRgba, mRgbaFiltered, Imgproc.COLOR_BGR2HSV);
        Log.d("hsv", "" + mBlobColorHsv.val[0] + ", " + mBlobColorHsv.val[1] + ", " + mBlobColorHsv.val[2]);
        Log.i("rgb", "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");
        mDetector.setHsvColor(mBlobColorHsv);
        mDetector.process(mRgba);
        List<MatOfPoint> contours = mDetector.getContours();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.05;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());
            Rect rect = Imgproc.boundingRect(points);
            Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);
        }
        return mRgba;
    }

    private Scalar converScalarRgba2HSV(Scalar rgba) {
        Mat  pointMatHsv= new Mat();
        Mat pointMatRgba = new Mat(1, 1, CvType.CV_8UC3, rgba);
        Imgproc.cvtColor(pointMatRgba,pointMatHsv, Imgproc.COLOR_RGB2HSV_FULL, 4);
        return new Scalar(pointMatHsv.get(0, 0));
    }
}
