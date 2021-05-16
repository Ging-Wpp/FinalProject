package com.example.finalproject;

import java.util.HashMap;
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

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import www.sanju.motiontoast.MotionToast;

public class detectobjbycam extends AppCompatActivity implements OnTouchListener, CvCameraViewListener2 {
    private static final String TAG = "detectobjbycam";

    private boolean mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private ColorBlobDetector mDetector;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;

    private TextView rgb;
    private TextView HexCode;
    private TextView Name;

    View ColorView;
    Bitmap bitmap;
    private ClipData clipData;
    private ClipboardManager clipboardManager;
    private CameraBridgeViewBase mOpenCvCameraView;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;

    FloatingActionButton mAddAlarmFab, mAddPersonFab;
    ExtendedFloatingActionButton mAddFab;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
                mOpenCvCameraView.setOnTouchListener(detectobjbycam.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    public detectobjbycam() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("QueryPermissionsNeeded")
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

        rgb = findViewById(R.id.resultTv);
        HexCode = findViewById(R.id.hex);
        Name = findViewById(R.id.name);

        final ImageButton camera = findViewById(R.id.camera);
        final ImageButton gallery = findViewById(R.id.gallery);

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

        camera.setOnClickListener(v -> {
            Intent intent1 = new Intent(detectobjbycam.this, detectobjbycam.class);
            startActivity(intent1);
        });

        gallery.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(detectobjbycam.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(detectobjbycam.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Toast.makeText(detectobjbycam.this, "Permission already granted", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Select photo from"), 1);
                }
            }
        });


    }//end oncreate

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(detectobjbycam.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(detectobjbycam.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(detectobjbycam.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(detectobjbycam.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == FindObj.RESULT_OK) {
            try {
                Intent intent = new Intent(detectobjbycam.this, detectColor.class);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                intent.putExtra("data", photo);
                startActivity(intent);
//                Intent intent1 = new Intent(FindObj.this, detectColor.class);
//                startActivity(intent1);
            } catch (Exception e) {
                Log.e("Log", "Error from Camera Activity");
            }
        }

        if (requestCode == 1 && resultCode == FindObj.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                Intent intent = new Intent(detectobjbycam.this, Fogallery.class);
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
        //Scalar กำหนดเป็นพื้นที่สีอื่น
        Scalar CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    // When a motion event happens (someone touches the device)
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "DefaultLocale"})
    public boolean onTouch(View v, MotionEvent event) {
        try {
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

            Log.d(TAG, touchedRegionRgba.toString());
            Log.d(TAG, touchedRegionHsv.toString());

            // Calculate average color of touched region
            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
            int pointCount = touchedRect.width * touchedRect.height;
            for (int i = 0; i < mBlobColorHsv.val.length; i++)
                mBlobColorHsv.val[i] /= pointCount;

            //converts scalar to hsv to RGB
            mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

            Log.d(TAG, "" + mBlobColorHsv.val[0] + ", " + mBlobColorHsv.val[1] + ", " + mBlobColorHsv.val[2]);

            Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                    ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

            mDetector.setHsvColor(mBlobColorHsv);

            // Resize the image to specture size
            Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);

            mIsColorSelected = true;
            //mOpenCvCameraView.setOnTouchListener(imgSourceOnTouchListener);


            // Release all mats
            touchedRegionRgba.release();
            touchedRegionHsv.release();

            String hex = String.format("#%02x%02x%02x", (int) mBlobColorRgba.val[0], (int) mBlobColorRgba.val[1], (int) mBlobColorRgba.val[2]);
//        mResultTv.setText("RGB: " + (int)mBlobColorRgba.val[0] + ", " + (int)mBlobColorRgba.val[1] + ", " + (int)mBlobColorRgba.val[2] + "\nHex Code: " + hex.toUpperCase());
//
            String colorName = getColorName(hex.substring(1));
            Log.d(TAG, colorName);

            rgb.setText(String.format("RGB: %d, %d, %d", (int) mBlobColorRgba.val[0], (int) mBlobColorRgba.val[1], (int) mBlobColorRgba.val[2]));
            HexCode.setText(String.format("HEX: %s", hex.toUpperCase()));

            Name.setText(colorName);

            ColorView = findViewById(R.id.colorView);
            ColorView.setBackgroundColor(Color.rgb((int) mBlobColorRgba.val[0], (int) mBlobColorRgba.val[1], (int) mBlobColorRgba.val[2]));

//        final FloatingActionButton fab = findViewById(R.id.add_fab);
//        TextView rgb = (TextView) findViewById(R.id.resultTv);
//        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        fab.setOnClickListener(v1 -> {
//            String txtcopy = rgb.getText().toString();
//            String copy = txtcopy.substring(5);
//            clipData = ClipData.newPlainText("text", copy);
//            clipboardManager.setPrimaryClip(clipData);
//            Toast.makeText(getApplicationContext(), copy, Toast.LENGTH_SHORT).show();
//        });

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

            //        final FloatingActionButton fab = findViewById(R.id.add_fab);
//        mAddPersonFab.setOnClickListener(v1 -> {
//            String txtcopy = rgb.getText().toString();
//            String copy = txtcopy.substring(5);
//            clipData = ClipData.newPlainText("text", copy);
//            clipboardManager.setPrimaryClip(clipData);
//            Toast.makeText(getApplicationContext(), copy, Toast.LENGTH_SHORT).show();
//        });

            ImageView logo = (ImageView) findViewById(R.id.imageView4);
            logo.setOnClickListener(view -> {
                Intent intent = new Intent(detectobjbycam.this, MainActivity.class);
                startActivity(intent);
            });
            TextView find = (TextView) findViewById(R.id.textView3);
            find.setOnClickListener(view -> {
                Intent intent = new Intent(detectobjbycam.this, MainActivity.class);
                startActivity(intent);
            });

            TextView rgb = (TextView) findViewById(R.id.resultTv);
            TextView hexcp = (TextView) findViewById(R.id.hex);
            clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

            mAddPersonFab.setOnClickListener(view -> {
                String txtcopy = rgb.getText().toString();
                String copy = txtcopy.substring(5);
                clipData = ClipData.newPlainText("text", copy);
                clipboardManager.setPrimaryClip(clipData);
//                    Toast.makeText(detectobjbycam.this, copy, Toast.LENGTH_SHORT).show();
                MotionToast.Companion.darkColorToast(detectobjbycam.this, "RGB: " + copy, MotionToast.TOAST_SUCCESS, MotionToast.GRAVITY_CENTER, MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(detectobjbycam.this, R.font.helvetica_regular));
            });
            mAddAlarmFab.setOnClickListener(view -> {
                String txtcopy = hexcp.getText().toString();
                String copy2 = txtcopy.substring(5);
                clipData = ClipData.newPlainText("text2", copy2);
                clipboardManager.setPrimaryClip(clipData);
//                    Toast.makeText(detectobjbycam.this, copy2, Toast.LENGTH_SHORT).show();
                MotionToast.Companion.darkColorToast(detectobjbycam.this, "Hex: " + copy2.toUpperCase(), MotionToast.TOAST_SUCCESS, MotionToast.GRAVITY_CENTER, MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(detectobjbycam.this, R.font.helvetica_regular));
            });

//        final ImageButton copyHex = (ImageButton) findViewById(R.id.copy);
//        TextView hexcopy = (TextView) findViewById(R.id.hex);
//        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        copyHex.setOnClickListener(v1 -> {
//            String copyhex = hexcopy.getText().toString();
//            String copy = copyhex.substring(10);
//            clipData = ClipData.newPlainText("text", copy);
//            clipboardManager.setPrimaryClip(clipData);
//            Toast.makeText(getApplicationContext(), copy, Toast.LENGTH_SHORT).show();
//        });
        }
        catch (Exception e) {
            MotionToast.Companion.darkColorToast(detectobjbycam.this, "Please pick color in camera", MotionToast.TOAST_WARNING, MotionToast.GRAVITY_CENTER, MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(detectobjbycam.this, R.font.helvetica_regular));
        }
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
            for (int i = 0; i < contours.size(); i++) {

//                Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
//                Mat colorLabel = mRgba.submat(20, 68, 20, 278);
//                colorLabel.setTo(mBlobColorRgba);
//                Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
//                mSpectrum.copyTo(spectrumLabel);

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
//                Core.rectangle(contoursFrame, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), (255, 0, 0, 255), 3);
                Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
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

    private String nearestColor(String code, HashMap<String, String> sColorNameMap) {
        int[] rgb = getRgb(code);
        double nearestDistance = Double.MAX_VALUE;
        String nearestNamedColorCode = null;
        for (String namedColorCode : sColorNameMap.keySet()) {
            int[] namedColorRgb = getRgb(namedColorCode);
            double distance = calculateDistance(rgb, namedColorRgb);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNamedColorCode = namedColorCode;
            }
        }
        return sColorNameMap.get(nearestNamedColorCode);
    }

    private static int[] getRgb(String code) {
        int r = Integer.parseInt(code.substring(0, 2), 16);
        int g = Integer.parseInt(code.substring(2, 4), 16);
        int b = Integer.parseInt(code.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    private static double calculateDistance(int[] rgb1, int[] rgb2) {
        double sum = 0.0;
        for (int i = 0; i < 3; i++) {
            sum += Math.pow(rgb2[i] - rgb1[i], 2);
        }
        return Math.sqrt(sum);
    }

    public String getColorName(String hexCode) {
        HashMap<String, String> color_name = new HashMap<>();
        color_name.put("FFFFE0", "LightYellow");
        color_name.put("FFFAFA", "Snow");
        color_name.put("FFFAF0", "FloralWhite");
        color_name.put("FFF8DC", "Cornsilk");
        color_name.put("000000", "Black");
        color_name.put("000080", "Navy Blue");
        color_name.put("0000C8", "Dark Blue");
        color_name.put("0000FF", "Blue");
        color_name.put("000741", "Stratos");
        color_name.put("001B1C", "Swamp");
        color_name.put("002387", "Resolution Blue");
        color_name.put("002900", "Deep Fir");
        color_name.put("002E20", "Burnham");
        color_name.put("002FA7", "International Klein Blue");
        color_name.put("003153", "Prussian Blue");
        color_name.put("003366", "Midnight Blue");
        color_name.put("003399", "Smalt");
        color_name.put("003532", "Deep Teal");
        color_name.put("003E40", "Cyprus");
        color_name.put("004620", "Kaitoke Green");
        color_name.put("0047AB", "Cobalt");
        color_name.put("004816", "Crusoe");
        color_name.put("004950", "Sherpa Blue");
        color_name.put("0056A7", "Endeavour");
        color_name.put("00581A", "Camarone");
        color_name.put("0066CC", "Science Blue");
        color_name.put("0066FF", "Blue Ribbon");
        color_name.put("00755E", "Tropical Rain Forest");
        color_name.put("0076A3", "Allports");
        color_name.put("007BA7", "Deep Cerulean");
        color_name.put("007EC7", "Lochmara");
        color_name.put("007FFF", "Azure Radiance");
        color_name.put("008080", "Teal");
        color_name.put("0095B6", "Bondi Blue");
        color_name.put("009DC4", "Pacific Blue");
        color_name.put("00A693", "Persian Green");
        color_name.put("00A86B", "Jade");
        color_name.put("00CC99", "Caribbean Green");
        color_name.put("00CCCC", "Robin's Egg Blue");
        color_name.put("00FF00", "Green");
        color_name.put("00FF7F", "Spring Green");
        color_name.put("00FFFF", "Cyan / Aqua");
        color_name.put("010D1A", "Blue Charcoal");
        color_name.put("011635", "Midnight");
        color_name.put("011D13", "Holly");
        color_name.put("012731", "Daintree");
        color_name.put("01361C", "Cardin Green");
        color_name.put("01371A", "County Green");
        color_name.put("013E62", "Astronaut Blue");
        color_name.put("013F6A", "Regal Blue");
        color_name.put("014B43", "Aqua Deep");
        color_name.put("015E85", "Orient");
        color_name.put("016162", "Blue Stone");
        color_name.put("016D39", "Fun Green");
        color_name.put("01796F", "Pine Green");
        color_name.put("017987", "Blue Lagoon");
        color_name.put("01826B", "Deep Sea");
        color_name.put("01A368", "Green Haze");
        color_name.put("022D15", "English Holly");
        color_name.put("02402C", "Sherwood Green");
        color_name.put("02478E", "Congress Blue");
        color_name.put("024E46", "Evening Sea");
        color_name.put("026395", "Bahama Blue");
        color_name.put("02866F", "Observatory");
        color_name.put("02A4D3", "Cerulean");
        color_name.put("03163C", "Tangaroa");
        color_name.put("032B52", "Green Vogue");
        color_name.put("036A6E", "Mosque");
        color_name.put("041004", "Midnight Moss");
        color_name.put("041322", "Black Pearl");
        color_name.put("042E4C", "Blue Whale");
        color_name.put("044022", "Zuccini");
        color_name.put("044259", "Teal Blue");
        color_name.put("051040", "Deep Cove");
        color_name.put("051657", "Gulf Blue");
        color_name.put("055989", "Venice Blue");
        color_name.put("056F57", "Watercourse");
        color_name.put("062A78", "Catalina Blue");
        color_name.put("063537", "Tiber");
        color_name.put("069B81", "Gossamer");
        color_name.put("06A189", "Niagara");
        color_name.put("073A50", "Tarawera");
        color_name.put("080110", "Jaguar");
        color_name.put("081910", "Black Bean");
        color_name.put("082567", "Deep Sapphire");
        color_name.put("088370", "Elf Green");
        color_name.put("08E8DE", "Bright Turquoise");
        color_name.put("092256", "Downriver");
        color_name.put("09230F", "Palm Green");
        color_name.put("09255D", "Madison");
        color_name.put("093624", "Bottle Green");
        color_name.put("095859", "Deep Sea Green");
        color_name.put("097F4B", "Salem");
        color_name.put("0A001C", "Black Russian");
        color_name.put("0A480D", "Dark Fern");
        color_name.put("0A6906", "Japanese Laurel");
        color_name.put("0A6F75", "Atoll");
        color_name.put("0B0B0B", "Cod Gray");
        color_name.put("0B0F08", "Marshland");
        color_name.put("0B1107", "Gordons Green");
        color_name.put("0B1304", "Black Forest");
        color_name.put("0B6207", "San Felix");
        color_name.put("0BDA51", "Malachite");
        color_name.put("0C0B1D", "Ebony");
        color_name.put("0C0D0F", "Woodsmoke");
        color_name.put("0C1911", "Racing Green");
        color_name.put("0C7A79", "Surfie Green");
        color_name.put("0C8990", "Blue Chill");
        color_name.put("0D0332", "Black Rock");
        color_name.put("0D1117", "Bunker");
        color_name.put("0D1C19", "Aztec");
        color_name.put("0D2E1C", "Bush");
        color_name.put("0E0E18", "Cinder");
        color_name.put("0E2A30", "Firefly");
        color_name.put("0F2D9E", "Torea Bay");
        color_name.put("10121D", "Vulcan");
        color_name.put("101405", "Green Waterloo");
        color_name.put("105852", "Eden");
        color_name.put("110C6C", "Arapawa");
        color_name.put("120A8F", "Ultramarine");
        color_name.put("123447", "Elephant");
        color_name.put("126B40", "Jewel");
        color_name.put("130000", "Diesel");
        color_name.put("130A06", "Asphalt");
        color_name.put("13264D", "Blue Zodiac");
        color_name.put("134F19", "Parsley");
        color_name.put("140600", "Nero");
        color_name.put("1450AA", "Tory Blue");
        color_name.put("151F4C", "Bunting");
        color_name.put("1560BD", "Denim");
        color_name.put("15736B", "Genoa");
        color_name.put("161928", "Mirage");
        color_name.put("161D10", "Hunter Green");
        color_name.put("162A40", "Big Stone");
        color_name.put("163222", "Celtic");
        color_name.put("16322C", "Timber Green");
        color_name.put("163531", "Gable Green");
        color_name.put("171F04", "Pine Tree");
        color_name.put("175579", "Chathams Blue");
        color_name.put("182D09", "Deep Forest Green");
        color_name.put("18587A", "Blumine");
        color_name.put("19330E", "Palm Leaf");
        color_name.put("193751", "Nile Blue");
        color_name.put("1959A8", "Fun Blue");
        color_name.put("1A1A68", "Lucky Point");
        color_name.put("1AB385", "Mountain Meadow");
        color_name.put("1B0245", "Tolopea");
        color_name.put("1B1035", "Haiti");
        color_name.put("1B127B", "Deep Koamaru");
        color_name.put("1B1404", "Acadia");
        color_name.put("1B2F11", "Seaweed");
        color_name.put("1B3162", "Biscay");
        color_name.put("1B659D", "Matisse");
        color_name.put("1C1208", "Crowshead");
        color_name.put("1C1E13", "Rangoon Green");
        color_name.put("1C39BB", "Persian Blue");
        color_name.put("1C402E", "Everglade");
        color_name.put("1C7C7D", "Elm");
        color_name.put("1D6142", "Green Pea");
        color_name.put("1E0F04", "Creole");
        color_name.put("1E1609", "Karaka");
        color_name.put("1E1708", "El Paso");
        color_name.put("1E385B", "Cello");
        color_name.put("1E433C", "Te Papa Green");
        color_name.put("1E90FF", "Dodger Blue");
        color_name.put("1E9AB0", "Eastern Blue");
        color_name.put("1F120F", "Night Rider");
        color_name.put("1FC2C2", "Java");
        color_name.put("20208D", "Jacksons Purple");
        color_name.put("202E54", "Cloud Burst");
        color_name.put("204852", "Blue Dianne");
        color_name.put("211A0E", "Eternity");
        color_name.put("220878", "Deep Blue");
        color_name.put("228B22", "Forest Green");
        color_name.put("233418", "Mallard");
        color_name.put("240A40", "Violet");
        color_name.put("240C02", "Kilamanjaro");
        color_name.put("242A1D", "Log Cabin");
        color_name.put("242E16", "Black Olive");
        color_name.put("24500F", "Green House");
        color_name.put("251607", "Graphite");
        color_name.put("251706", "Cannon Black");
        color_name.put("251F4F", "Port Gore");
        color_name.put("25272C", "Shark");
        color_name.put("25311C", "Green Kelp");
        color_name.put("2596D1", "Curious Blue");
        color_name.put("260368", "Paua");
        color_name.put("26056A", "Paris M");
        color_name.put("261105", "Wood Bark");
        color_name.put("261414", "Gondola");
        color_name.put("262335", "Steel Gray");
        color_name.put("26283B", "Ebony Clay");
        color_name.put("273A81", "Bay of Many");
        color_name.put("27504B", "Plantation");
        color_name.put("278A5B", "Eucalyptus");
        color_name.put("281E15", "Oil");
        color_name.put("283A77", "Astronaut");
        color_name.put("286ACD", "Mariner");
        color_name.put("290C5E", "Violent Violet");
        color_name.put("292130", "Bastille");
        color_name.put("292319", "Zeus");
        color_name.put("292937", "Charade");
        color_name.put("297B9A", "Jelly Bean");
        color_name.put("29AB87", "Jungle Green");
        color_name.put("2A0359", "Cherry Pie");
        color_name.put("2A140E", "Coffee Bean");
        color_name.put("2A2630", "Baltic Sea");
        color_name.put("2A380B", "Turtle Green");
        color_name.put("2A52BE", "Cerulean Blue");
        color_name.put("2B0202", "Sepia Black");
        color_name.put("2B194F", "Valhalla");
        color_name.put("2B3228", "Heavy Metal");
        color_name.put("2C0E8C", "Blue Gem");
        color_name.put("2C1632", "Revolver");
        color_name.put("2C2133", "Bleached Cedar");
        color_name.put("2C8C84", "Lochinvar");
        color_name.put("2D2510", "Mikado");
        color_name.put("2D383A", "Outer Space");
        color_name.put("2D569B", "St Tropaz");
        color_name.put("2E0329", "Jacaranda");
        color_name.put("2E1905", "Jacko Bean");
        color_name.put("2E3222", "Rangitoto");
        color_name.put("2E3F62", "Rhino");
        color_name.put("2E8B57", "Sea Green");
        color_name.put("2EBFD4", "Scooter");
        color_name.put("2F270E", "Onion");
        color_name.put("2F3CB3", "Governor Bay");
        color_name.put("2F519E", "Sapphire");
        color_name.put("2F5A57", "Spectra");
        color_name.put("2F6168", "Casal");
        color_name.put("300529", "Melanzane");
        color_name.put("301F1E", "Cocoa Brown");
        color_name.put("302A0F", "Woodrush");
        color_name.put("304B6A", "San Juan");
        color_name.put("30D5C8", "Turquoise");
        color_name.put("311C17", "Eclipse");
        color_name.put("314459", "Pickled Bluewood");
        color_name.put("315BA1", "Azure");
        color_name.put("31728D", "Calypso");
        color_name.put("317D82", "Paradiso");
        color_name.put("32127A", "Persian Indigo");
        color_name.put("32293A", "Blackcurrant");
        color_name.put("323232", "Mine Shaft");
        color_name.put("325D52", "Stromboli");
        color_name.put("327C14", "Bilbao");
        color_name.put("327DA0", "Astral");
        color_name.put("33036B", "Christalle");
        color_name.put("33292F", "Thunder");
        color_name.put("33CC99", "Shamrock");
        color_name.put("341515", "Tamarind");
        color_name.put("350036", "Mardi Gras");
        color_name.put("350E42", "Valentino");
        color_name.put("350E57", "Jagger");
        color_name.put("353542", "Tuna");
        color_name.put("354E8C", "Chambray");
        color_name.put("363050", "Martinique");
        color_name.put("363534", "Tuatara");
        color_name.put("363C0D", "Waiouru");
        color_name.put("36747D", "Ming");
        color_name.put("368716", "La Palma");
        color_name.put("370202", "Chocolate");
        color_name.put("371D09", "Clinker");
        color_name.put("37290E", "Brown Tumbleweed");
        color_name.put("373021", "Birch");
        color_name.put("377475", "Oracle");
        color_name.put("380474", "Blue Diamond");
        color_name.put("381A51", "Grape");
        color_name.put("383533", "Dune");
        color_name.put("384555", "Oxford Blue");
        color_name.put("384910", "Clover");
        color_name.put("394851", "Limed Spruce");
        color_name.put("396413", "Dell");
        color_name.put("3A0020", "Toledo");
        color_name.put("3A2010", "Sambuca");
        color_name.put("3A2A6A", "Jacarta");
        color_name.put("3A686C", "William");
        color_name.put("3A6A47", "Killarney");
        color_name.put("3AB09E", "Keppel");
        color_name.put("3B000B", "Temptress");
        color_name.put("3B0910", "Aubergine");
        color_name.put("3B1F1F", "Jon");
        color_name.put("3B2820", "Treehouse");
        color_name.put("3B7A57", "Amazon");
        color_name.put("3B91B4", "Boston Blue");
        color_name.put("3C0878", "Windsor");
        color_name.put("3C1206", "Rebel");
        color_name.put("3C1F76", "Meteorite");
        color_name.put("3C2005", "Dark Ebony");
        color_name.put("3C3910", "Camouflage");
        color_name.put("3C4151", "Bright Gray");
        color_name.put("3C4443", "Cape Cod");
        color_name.put("3C493A", "Lunar Green");
        color_name.put("3D0C02", "Bean");
        color_name.put("3D2B1F", "Bistre");
        color_name.put("3D7D52", "Goblin");
        color_name.put("3E0480", "Kingfisher Daisy");
        color_name.put("3E1C14", "Cedar");
        color_name.put("3E2B23", "English Walnut");
        color_name.put("3E2C1C", "Black Marlin");
        color_name.put("3E3A44", "Ship Gray");
        color_name.put("3EABBF", "Pelorous");
        color_name.put("3F2109", "Bronze");
        color_name.put("3F2500", "Cola");
        color_name.put("3F3002", "Madras");
        color_name.put("3F307F", "Minsk");
        color_name.put("3F4C3A", "Cabbage Pont");
        color_name.put("3F583B", "Tom Thumb");
        color_name.put("3F5D53", "Mineral Green");
        color_name.put("3FC1AA", "Puerto Rico");
        color_name.put("3FFF00", "Harlequin");
        color_name.put("401801", "Brown Pod");
        color_name.put("40291D", "Cork");
        color_name.put("403B38", "Masala");
        color_name.put("403D19", "Thatch Green");
        color_name.put("405169", "Fiord");
        color_name.put("40826D", "Viridian");
        color_name.put("40A860", "Chateau Green");
        color_name.put("410056", "Ripe Plum");
        color_name.put("411F10", "Paco");
        color_name.put("412010", "Deep Oak");
        color_name.put("413C37", "Merlin");
        color_name.put("414257", "Gun Powder");
        color_name.put("414C7D", "East Bay");
        color_name.put("4169E1", "Royal Blue");
        color_name.put("41AA78", "Ocean Green");
        color_name.put("420303", "Burnt Maroon");
        color_name.put("423921", "Lisbon Brown");
        color_name.put("427977", "Faded Jade");
        color_name.put("431560", "Scarlet Gum");
        color_name.put("433120", "Iroko");
        color_name.put("433E37", "Armadillo");
        color_name.put("434C59", "River Bed");
        color_name.put("436A0D", "Green Leaf");
        color_name.put("44012D", "Barossa");
        color_name.put("441D00", "Morocco Brown");
        color_name.put("444954", "Mako");
        color_name.put("454936", "Kelp");
        color_name.put("456CAC", "San Marino");
        color_name.put("45B1E8", "Picton Blue");
        color_name.put("460B41", "Loulou");
        color_name.put("462425", "Crater Brown");
        color_name.put("465945", "Gray Asparagus");
        color_name.put("4682B4", "Steel Blue");
        color_name.put("480404", "Rustic Red");
        color_name.put("480607", "Bulgarian Rose");
        color_name.put("480656", "Clairvoyant");
        color_name.put("481C1C", "Cocoa Bean");
        color_name.put("483131", "Woody Brown");
        color_name.put("483C32", "Taupe");
        color_name.put("49170C", "Van Cleef");
        color_name.put("492615", "Brown Derby");
        color_name.put("49371B", "Metallic Bronze");
        color_name.put("495400", "Verdun Green");
        color_name.put("496679", "Blue Bayoux");
        color_name.put("497183", "Bismark");
        color_name.put("4A2A04", "Bracken");
        color_name.put("4A3004", "Deep Bronze");
        color_name.put("4A3C30", "Mondo");
        color_name.put("4A4244", "Tundora");
        color_name.put("4A444B", "Gravel");
        color_name.put("4A4E5A", "Trout");
        color_name.put("4B0082", "Pigment Indigo");
        color_name.put("4B5D52", "Nandor");
        color_name.put("4C3024", "Saddle");
        color_name.put("4C4F56", "Abbey");
        color_name.put("4D0135", "Blackberry");
        color_name.put("4D0A18", "Cab Sav");
        color_name.put("4D1E01", "Indian Tan");
        color_name.put("4D282D", "Cowboy");
        color_name.put("4D282E", "Livid Brown");
        color_name.put("4D3833", "Rock");
        color_name.put("4D3D14", "Punga");
        color_name.put("4D400F", "Bronzetone");
        color_name.put("4D5328", "Woodland");
        color_name.put("4E0606", "Mahogany");
        color_name.put("4E2A5A", "Bossanova");
        color_name.put("4E3B41", "Matterhorn");
        color_name.put("4E420C", "Bronze Olive");
        color_name.put("4E4562", "Mulled Wine");
        color_name.put("4E6649", "Axolotl");
        color_name.put("4E7F9E", "Wedgewood");
        color_name.put("4EABD1", "Shakespeare");
        color_name.put("4F1C70", "Honey Flower");
        color_name.put("4F2398", "Daisy Bush");
        color_name.put("4F69C6", "Indigo");
        color_name.put("4F7942", "Fern Green");
        color_name.put("4F9D5D", "Fruit Salad");
        color_name.put("4FA83D", "Apple");
        color_name.put("504351", "Mortar");
        color_name.put("507096", "Kashmir Blue");
        color_name.put("507672", "Cutty Sark");
        color_name.put("50C878", "Emerald");
        color_name.put("514649", "Emperor");
        color_name.put("516E3D", "Chalet Green");
        color_name.put("517C66", "Como");
        color_name.put("51808F", "Smalt Blue");
        color_name.put("52001F", "Castro");
        color_name.put("520C17", "Maroon Oak");
        color_name.put("523C94", "Gigas");
        color_name.put("533455", "Voodoo");
        color_name.put("534491", "Victoria");
        color_name.put("53824B", "Hippie Green");
        color_name.put("541012", "Heath");
        color_name.put("544333", "Judge Gray");
        color_name.put("54534D", "Fuscous Gray");
        color_name.put("549019", "Vida Loca");
        color_name.put("55280C", "Cioccolato");
        color_name.put("555B10", "Saratoga");
        color_name.put("556D56", "Finlandia");
        color_name.put("5590D9", "Havelock Blue");
        color_name.put("56B4BE", "Fountain Blue");
        color_name.put("578363", "Spring Leaves");
        color_name.put("583401", "Saddle Brown");
        color_name.put("585562", "Scarpa Flow");
        color_name.put("587156", "Cactus");
        color_name.put("589AAF", "Hippie Blue");
        color_name.put("591D35", "Wine Berry");
        color_name.put("592804", "Brown Bramble");
        color_name.put("593737", "Congo Brown");
        color_name.put("594433", "Millbrook");
        color_name.put("5A6E9C", "Waikawa Gray");
        color_name.put("5A87A0", "Horizon");
        color_name.put("5B3013", "Jambalaya");
        color_name.put("5C0120", "Bordeaux");
        color_name.put("5C0536", "Mulberry Wood");
        color_name.put("5C2E01", "Carnaby Tan");
        color_name.put("5C5D75", "Comet");
        color_name.put("5D1E0F", "Redwood");
        color_name.put("5D4C51", "Don Juan");
        color_name.put("5D5C58", "Chicago");
        color_name.put("5D5E37", "Verdigris");
        color_name.put("5D7747", "Dingley");
        color_name.put("5DA19F", "Breaker Bay");
        color_name.put("5E483E", "Kabul");
        color_name.put("5E5D3B", "Hemlock");
        color_name.put("5F3D26", "Irish Coffee");
        color_name.put("5F5F6E", "Mid Gray");
        color_name.put("5F6672", "Shuttle Gray");
        color_name.put("5FA777", "Aqua Forest");
        color_name.put("5FB3AC", "Tradewind");
        color_name.put("604913", "Horses Neck");
        color_name.put("605B73", "Smoky");
        color_name.put("606E68", "Corduroy");
        color_name.put("6093D1", "Danube");
        color_name.put("612718", "Espresso");
        color_name.put("614051", "Eggplant");
        color_name.put("615D30", "Costa Del Sol");
        color_name.put("61845F", "Glade Green");
        color_name.put("622F30", "Buccaneer");
        color_name.put("623F2D", "Quincy");
        color_name.put("624E9A", "Butterfly Bush");
        color_name.put("625119", "West Coast");
        color_name.put("626649", "Finch");
        color_name.put("639A8F", "Patina");
        color_name.put("63B76C", "Fern");
        color_name.put("6456B7", "Blue Violet");
        color_name.put("646077", "Dolphin");
        color_name.put("646463", "Storm Dust");
        color_name.put("646A54", "Siam");
        color_name.put("646E75", "Nevada");
        color_name.put("6495ED", "Cornflower Blue");
        color_name.put("64CCDB", "Viking");
        color_name.put("65000B", "Rosewood");
        color_name.put("651A14", "Cherrywood");
        color_name.put("652DC1", "Purple Heart");
        color_name.put("657220", "Fern Frond");
        color_name.put("65745D", "Willow Grove");
        color_name.put("65869F", "Hoki");
        color_name.put("660045", "Pompadour");
        color_name.put("660099", "Purple");
        color_name.put("66023C", "Tyrian Purple");
        color_name.put("661010", "Dark Tan");
        color_name.put("66B58F", "Silver Tree");
        color_name.put("66FF00", "Bright Green");
        color_name.put("66FF66", "Screamin' Green");
        color_name.put("67032D", "Black Rose");
        color_name.put("675FA6", "Scampi");
        color_name.put("676662", "Ironside Gray");
        color_name.put("678975", "Viridian Green");
        color_name.put("67A712", "Christi");
        color_name.put("683600", "Nutmeg Wood Finish");
        color_name.put("685558", "Zambezi");
        color_name.put("685E6E", "Salt Box");
        color_name.put("692545", "Tawny Port");
        color_name.put("692D54", "Finn");
        color_name.put("695F62", "Scorpion");
        color_name.put("697E9A", "Lynch");
        color_name.put("6A442E", "Spice");
        color_name.put("6A5D1B", "Himalaya");
        color_name.put("6A6051", "Soya Bean");
        color_name.put("6B2A14", "Hairy Heath");
        color_name.put("6B3FA0", "Royal Purple");
        color_name.put("6B4E31", "Shingle Fawn");
        color_name.put("6B5755", "Dorado");
        color_name.put("6B8BA2", "Bermuda Gray");
        color_name.put("6B8E23", "Olive Drab");
        color_name.put("6C3082", "Eminence");
        color_name.put("6CDAE7", "Turquoise Blue");
        color_name.put("6D0101", "Lonestar");
        color_name.put("6D5E54", "Pine Cone");
        color_name.put("6D6C6C", "Dove Gray");
        color_name.put("6D9292", "Juniper");
        color_name.put("6D92A1", "Gothic");
        color_name.put("6E0902", "Red Oxide");
        color_name.put("6E1D14", "Moccaccino");
        color_name.put("6E4826", "Pickled Bean");
        color_name.put("6E4B26", "Dallas");
        color_name.put("6E6D57", "Kokoda");
        color_name.put("6E7783", "Pale Sky");
        color_name.put("6F440C", "Cafe Royale");
        color_name.put("6F6A61", "Flint");
        color_name.put("6F8E63", "Highland");
        color_name.put("6F9D02", "Limeade");
        color_name.put("6FD0C5", "Downy");
        color_name.put("701C1C", "Persian Plum");
        color_name.put("704214", "Sepia");
        color_name.put("704A07", "Antique Bronze");
        color_name.put("704F50", "Ferra");
        color_name.put("706555", "Coffee");
        color_name.put("708090", "Slate Gray");
        color_name.put("711A00", "Cedar Wood Finish");
        color_name.put("71291D", "Metallic Copper");
        color_name.put("714693", "Affair");
        color_name.put("714AB2", "Studio");
        color_name.put("715D47", "Tobacco Brown");
        color_name.put("716338", "Yellow Metal");
        color_name.put("716B56", "Peat");
        color_name.put("716E10", "Olivetone");
        color_name.put("717486", "Storm Gray");
        color_name.put("718080", "Sirocco");
        color_name.put("71D9E2", "Aquamarine Blue");
        color_name.put("72010F", "Venetian Red");
        color_name.put("724A2F", "Old Copper");
        color_name.put("726D4E", "Go Ben");
        color_name.put("727B89", "Raven");
        color_name.put("731E8F", "Seance");
        color_name.put("734A12", "Raw Umber");
        color_name.put("736C9F", "Kimberly");
        color_name.put("736D58", "Crocodile");
        color_name.put("737829", "Crete");
        color_name.put("738678", "Xanadu");
        color_name.put("74640D", "Spicy Mustard");
        color_name.put("747D63", "Limed Ash");
        color_name.put("747D83", "Rolling Stone");
        color_name.put("748881", "Blue Smoke");
        color_name.put("749378", "Laurel");
        color_name.put("74C365", "Mantis");
        color_name.put("755A57", "Russett");
        color_name.put("7563A8", "Deluge");
        color_name.put("76395D", "Cosmic");
        color_name.put("7666C6", "Blue Marguerite");
        color_name.put("76BD17", "Lima");
        color_name.put("76D7EA", "Sky Blue");
        color_name.put("770F05", "Dark Burgundy");
        color_name.put("771F1F", "Crown of Thorns");
        color_name.put("773F1A", "Walnut");
        color_name.put("776F61", "Pablo");
        color_name.put("778120", "Pacifika");
        color_name.put("779E86", "Oxley");
        color_name.put("77DD77", "Pastel Green");
        color_name.put("780109", "Japanese Maple");
        color_name.put("782D19", "Mocha");
        color_name.put("782F16", "Peanut");
        color_name.put("78866B", "Camouflage Green");
        color_name.put("788A25", "Wasabi");
        color_name.put("788BBA", "Ship Cove");
        color_name.put("78A39C", "Sea Nymph");
        color_name.put("795D4C", "Roman Coffee");
        color_name.put("796878", "Old Lavender");
        color_name.put("796989", "Rum");
        color_name.put("796A78", "Fedora");
        color_name.put("796D62", "Sandstone");
        color_name.put("79DEEC", "Spray");
        color_name.put("7A013A", "Siren");
        color_name.put("7A58C1", "Fuchsia Blue");
        color_name.put("7A7A7A", "Boulder");
        color_name.put("7A89B8", "Wild Blue Yonder");
        color_name.put("7AC488", "De York");
        color_name.put("7B3801", "Red Beech");
        color_name.put("7B3F00", "Cinnamon");
        color_name.put("7B6608", "Yukon Gold");
        color_name.put("7B7874", "Tapa");
        color_name.put("7B7C94", "Waterloo");
        color_name.put("7B8265", "Flax Smoke");
        color_name.put("7B9F80", "Amulet");
        color_name.put("7BA05B", "Asparagus");
        color_name.put("7C1C05", "Kenyan Copper");
        color_name.put("7C7631", "Pesto");
        color_name.put("7C778A", "Topaz");
        color_name.put("7C7B7A", "Concord");
        color_name.put("7C7B82", "Jumbo");
        color_name.put("7C881A", "Trendy Green");
        color_name.put("7CA1A6", "Gumbo");
        color_name.put("7CB0A1", "Acapulco");
        color_name.put("7CB7BB", "Neptune");
        color_name.put("7D2C14", "Pueblo");
        color_name.put("7DA98D", "Bay Leaf");
        color_name.put("7DC8F7", "Malibu");
        color_name.put("7DD8C6", "Bermuda");
        color_name.put("7E3A15", "Copper Canyon");
        color_name.put("7F1734", "Claret");
        color_name.put("7F3A02", "Peru Tan");
        color_name.put("7F626D", "Falcon");
        color_name.put("7F7589", "Mobster");
        color_name.put("7F76D3", "Moody Blue");
        color_name.put("7FFF00", "Chartreuse");
        color_name.put("7FFFD4", "Aquamarine");
        color_name.put("800000", "Maroon");
        color_name.put("800B47", "Rose Bud Cherry");
        color_name.put("801818", "Falu Red");
        color_name.put("80341F", "Red Robin");
        color_name.put("803790", "Vivid Violet");
        color_name.put("80461B", "Russet");
        color_name.put("807E79", "Friar Gray");
        color_name.put("808000", "Olive");
        color_name.put("808080", "Gray");
        color_name.put("80B3AE", "Gulf Stream");
        color_name.put("80B3C4", "Glacier");
        color_name.put("80CCEA", "Seagull");
        color_name.put("81422C", "Nutmeg");
        color_name.put("816E71", "Spicy Pink");
        color_name.put("817377", "Empress");
        color_name.put("819885", "Spanish Green");
        color_name.put("826F65", "Sand Dune");
        color_name.put("828685", "Gunsmoke");
        color_name.put("828F72", "Battleship Gray");
        color_name.put("831923", "Merlot");
        color_name.put("837050", "Shadow");
        color_name.put("83AA5D", "Chelsea Cucumber");
        color_name.put("83D0C6", "Monte Carlo");
        color_name.put("843179", "Plum");
        color_name.put("84A0A0", "Granny Smith");
        color_name.put("8581D9", "Chetwode Blue");
        color_name.put("858470", "Bandicoot");
        color_name.put("859FAF", "Bali Hai");
        color_name.put("85C4CC", "Half Baked");
        color_name.put("860111", "Red Devil");
        color_name.put("863C3C", "Lotus");
        color_name.put("86483C", "Ironstone");
        color_name.put("864D1E", "Bull Shot");
        color_name.put("86560A", "Rusty Nail");
        color_name.put("868974", "Bitter");
        color_name.put("86949F", "Regent Gray");
        color_name.put("871550", "Disco");
        color_name.put("87756E", "Americano");
        color_name.put("877C7B", "Hurricane");
        color_name.put("878D91", "Oslo Gray");
        color_name.put("87AB39", "Sushi");
        color_name.put("885342", "Spicy Mix");
        color_name.put("886221", "Kumera");
        color_name.put("888387", "Suva Gray");
        color_name.put("888D65", "Avocado");
        color_name.put("893456", "Camelot");
        color_name.put("893843", "Solid Pink");
        color_name.put("894367", "Cannon Pink");
        color_name.put("897D6D", "Makara");
        color_name.put("8A3324", "Burnt Umber");
        color_name.put("8A73D6", "True V");
        color_name.put("8A8389", "Monsoon");
        color_name.put("8A8F8A", "Stack");
        color_name.put("8AB9F1", "Jordy Blue");
        color_name.put("8B00FF", "Electric Violet");
        color_name.put("8B0723", "Monarch");
        color_name.put("8B6B0B", "Corn Harvest");
        color_name.put("8B8470", "Olive Haze");
        color_name.put("8B847E", "Schooner");
        color_name.put("8B8680", "Natural Gray");
        color_name.put("8B9C90", "Mantle");
        color_name.put("8B9FEE", "Portage");
        color_name.put("8BA690", "Envy");
        color_name.put("8BA9A5", "Cascade");
        color_name.put("8BE6D8", "Riptide");
        color_name.put("8C055E", "Cardinal Pink");
        color_name.put("8C472F", "Mule Fawn");
        color_name.put("8C5738", "Potters Clay");
        color_name.put("8C6495", "Trendy Pink");
        color_name.put("8D0226", "Paprika");
        color_name.put("8D3D38", "Sanguine Brown");
        color_name.put("8D3F3F", "Tosca");
        color_name.put("8D7662", "Cement");
        color_name.put("8D8974", "Granite Green");
        color_name.put("8D90A1", "Manatee");
        color_name.put("8DA8CC", "Polo Blue");
        color_name.put("8E0000", "Red Berry");
        color_name.put("8E4D1E", "Rope");
        color_name.put("8E6F70", "Opium");
        color_name.put("8E775E", "Domino");
        color_name.put("8E8190", "Mamba");
        color_name.put("8EABC1", "Nepal");
        color_name.put("8F021C", "Pohutukawa");
        color_name.put("8F3E33", "El Salva");
        color_name.put("8F4B0E", "Korma");
        color_name.put("8F8176", "Squirrel");
        color_name.put("8FD6B4", "Vista Blue");
        color_name.put("900020", "Burgundy");
        color_name.put("901E1E", "Old Brick");
        color_name.put("907874", "Hemp");
        color_name.put("907B71", "Almond Frost");
        color_name.put("908D39", "Sycamore");
        color_name.put("92000A", "Sangria");
        color_name.put("924321", "Cumin");
        color_name.put("926F5B", "Beaver");
        color_name.put("928573", "Stonewall");
        color_name.put("928590", "Venus");
        color_name.put("9370DB", "Medium Purple");
        color_name.put("93CCEA", "Cornflower");
        color_name.put("93DFB8", "Algae Green");
        color_name.put("944747", "Copper Rust");
        color_name.put("948771", "Arrowtown");
        color_name.put("950015", "Scarlett");
        color_name.put("956387", "Strikemaster");
        color_name.put("959396", "Mountain Mist");
        color_name.put("960018", "Carmine");
        color_name.put("964B00", "Brown");
        color_name.put("967059", "Leather");
        color_name.put("9678B6", "Purple Mountain's Majesty");
        color_name.put("967BB6", "Lavender Purple");
        color_name.put("96A8A1", "Pewter");
        color_name.put("96BBAB", "Summer Green");
        color_name.put("97605D", "Au Chico");
        color_name.put("9771B5", "Wisteria");
        color_name.put("97CD2D", "Atlantis");
        color_name.put("983D61", "Vin Rouge");
        color_name.put("9874D3", "Lilac Bush");
        color_name.put("98777B", "Bazaar");
        color_name.put("98811B", "Hacienda");
        color_name.put("988D77", "Pale Oyster");
        color_name.put("98FF98", "Mint Green");
        color_name.put("990066", "Fresh Eggplant");
        color_name.put("991199", "Violet Eggplant");
        color_name.put("991613", "Tamarillo");
        color_name.put("991B07", "Totem Pole");
        color_name.put("996666", "Copper Rose");
        color_name.put("9966CC", "Amethyst");
        color_name.put("997A8D", "Mountbatten Pink");
        color_name.put("9999CC", "Blue Bell");
        color_name.put("9A3820", "Prairie Sand");
        color_name.put("9A6E61", "Toast");
        color_name.put("9A9577", "Gurkha");
        color_name.put("9AB973", "Olivine");
        color_name.put("9AC2B8", "Shadow Green");
        color_name.put("9B4703", "Oregon");
        color_name.put("9B9E8F", "Lemon Grass");
        color_name.put("9C3336", "Stiletto");
        color_name.put("9D5616", "Hawaiian Tan");
        color_name.put("9DACB7", "Gull Gray");
        color_name.put("9DC209", "Pistachio");
        color_name.put("9DE093", "Granny Smith Apple");
        color_name.put("9DE5FF", "Anakiwa");
        color_name.put("9E5302", "Chelsea Gem");
        color_name.put("9E5B40", "Sepia Skin");
        color_name.put("9EA587", "Sage");
        color_name.put("9EA91F", "Citron");
        color_name.put("9EB1CD", "Rock Blue");
        color_name.put("9EDEE0", "Morning Glory");
        color_name.put("9F381D", "Cognac");
        color_name.put("9F821C", "Reef Gold");
        color_name.put("9F9F9C", "Star Dust");
        color_name.put("9FA0B1", "Santas Gray");
        color_name.put("9FD7D3", "Sinbad");
        color_name.put("9FDD8C", "Feijoa");
        color_name.put("A02712", "Tabasco");
        color_name.put("A1750D", "Buttered Rum");
        color_name.put("A1ADB5", "Hit Gray");
        color_name.put("A1C50A", "Citrus");
        color_name.put("A1DAD7", "Aqua Island");
        color_name.put("A1E9DE", "Water Leaf");
        color_name.put("A2006D", "Flirt");
        color_name.put("A23B6C", "Rouge");
        color_name.put("A26645", "Cape Palliser");
        color_name.put("A2AAB3", "Gray Chateau");
        color_name.put("A2AEAB", "Edward");
        color_name.put("A3807B", "Pharlap");
        color_name.put("A397B4", "Amethyst Smoke");
        color_name.put("A3E3ED", "Blizzard Blue");
        color_name.put("A4A49D", "Delta");
        color_name.put("A4A6D3", "Wistful");
        color_name.put("A4AF6E", "Green Smoke");
        color_name.put("A50B5E", "Jazzberry Jam");
        color_name.put("A59B91", "Zorba");
        color_name.put("A5CB0C", "Bahia");
        color_name.put("A62F20", "Roof Terracotta");
        color_name.put("A65529", "Paarl");
        color_name.put("A68B5B", "Barley Corn");
        color_name.put("A69279", "Donkey Brown");
        color_name.put("A6A29A", "Dawn");
        color_name.put("A72525", "Mexican Red");
        color_name.put("A7882C", "Luxor Gold");
        color_name.put("A85307", "Rich Gold");
        color_name.put("A86515", "Reno Sand");
        color_name.put("A86B6B", "Coral Tree");
        color_name.put("A8989B", "Dusty Gray");
        color_name.put("A899E6", "Dull Lavender");
        color_name.put("A8A589", "Tallow");
        color_name.put("A8AE9C", "Bud");
        color_name.put("A8AF8E", "Locust");
        color_name.put("A8BD9F", "Norway");
        color_name.put("A8E3BD", "Chinook");
        color_name.put("A9A491", "Gray Olive");
        color_name.put("A9ACB6", "Aluminium");
        color_name.put("A9B2C3", "Cadet Blue");
        color_name.put("A9B497", "Schist");
        color_name.put("A9BDBF", "Tower Gray");
        color_name.put("A9BEF2", "Perano");
        color_name.put("A9C6C2", "Opal");
        color_name.put("AA375A", "Night Shadz");
        color_name.put("AA4203", "Fire");
        color_name.put("AA8B5B", "Muesli");
        color_name.put("AA8D6F", "Sandal");
        color_name.put("AAA5A9", "Shady Lady");
        color_name.put("AAA9CD", "Logan");
        color_name.put("AAABB7", "Spun Pearl");
        color_name.put("AAD6E6", "Regent St Blue");
        color_name.put("AAF0D1", "Magic Mint");
        color_name.put("AB0563", "Lipstick");
        color_name.put("AB3472", "Royal Heath");
        color_name.put("AB917A", "Sandrift");
        color_name.put("ABA0D9", "Cold Purple");
        color_name.put("ABA196", "Bronco");
        color_name.put("AC8A56", "Limed Oak");
        color_name.put("AC91CE", "East Side");
        color_name.put("AC9E22", "Lemon Ginger");
        color_name.put("ACA494", "Napa");
        color_name.put("ACA586", "Hillary");
        color_name.put("ACA59F", "Cloudy");
        color_name.put("ACACAC", "Silver Chalice");
        color_name.put("ACB78E", "Swamp Green");
        color_name.put("ACCBB1", "Spring Rain");
        color_name.put("ACDD4D", "Conifer");
        color_name.put("ACE1AF", "Celadon");
        color_name.put("AD781B", "Mandalay");
        color_name.put("ADBED1", "Casper");
        color_name.put("ADDFAD", "Moss Green");
        color_name.put("ADE6C4", "Padua");
        color_name.put("ADFF2F", "Green Yellow");
        color_name.put("AE4560", "Hippie Pink");
        color_name.put("AE6020", "Desert");
        color_name.put("AE809E", "Bouquet");
        color_name.put("AF4035", "Medium Carmine");
        color_name.put("AF4D43", "Apple Blossom");
        color_name.put("AF593E", "Brown Rust");
        color_name.put("AF8751", "Driftwood");
        color_name.put("AF8F2C", "Alpine");
        color_name.put("AF9F1C", "Lucky");
        color_name.put("AFA09E", "Martini");
        color_name.put("AFB1B8", "Bombay");
        color_name.put("AFBDD9", "Pigeon Post");
        color_name.put("B04C6A", "Cadillac");
        color_name.put("B05D54", "Matrix");
        color_name.put("B05E81", "Tapestry");
        color_name.put("B06608", "Mai Tai");
        color_name.put("B09A95", "Del Rio");
        color_name.put("B0E0E6", "Powder Blue");
        color_name.put("B0E313", "Inch Worm");
        color_name.put("B10000", "Bright Red");
        color_name.put("B14A0B", "Vesuvius");
        color_name.put("B1610B", "Pumpkin Skin");
        color_name.put("B16D52", "Santa Fe");
        color_name.put("B19461", "Teak");
        color_name.put("B1E2C1", "Fringy Flower");
        color_name.put("B1F4E7", "Ice Cold");
        color_name.put("B20931", "Shiraz");
        color_name.put("B2A1EA", "Biloba Flower");
        color_name.put("B32D29", "Tall Poppy");
        color_name.put("B35213", "Fiery Orange");
        color_name.put("B38007", "Hot Toddy");
        color_name.put("B3AF95", "Taupe Gray");
        color_name.put("B3C110", "La Rioja");
        color_name.put("B43332", "Well Read");
        color_name.put("B44668", "Blush");
        color_name.put("B4CFD3", "Jungle Mist");
        color_name.put("B57281", "Turkish Rose");
        color_name.put("B57EDC", "Lavender");
        color_name.put("B5A27F", "Mongoose");
        color_name.put("B5B35C", "Olive Green");
        color_name.put("B5D2CE", "Jet Stream");
        color_name.put("B5ECDF", "Cruise");
        color_name.put("B6316C", "Hibiscus");
        color_name.put("B69D98", "Thatch");
        color_name.put("B6B095", "Heathered Gray");
        color_name.put("B6BAA4", "Eagle");
        color_name.put("B6D1EA", "Spindle");
        color_name.put("B6D3BF", "Gum Leaf");
        color_name.put("B7410E", "Rust");
        color_name.put("B78E5C", "Muddy Waters");
        color_name.put("B7A214", "Sahara");
        color_name.put("B7A458", "Husk");
        color_name.put("B7B1B1", "Nobel");
        color_name.put("B7C3D0", "Heather");
        color_name.put("B7F0BE", "Madang");
        color_name.put("B81104", "Milano Red");
        color_name.put("B87333", "Copper");
        color_name.put("B8B56A", "Gimblet");
        color_name.put("B8C1B1", "Green Spring");
        color_name.put("B8C25D", "Celery");
        color_name.put("B8E0F9", "Sail");
        color_name.put("B94E48", "Chestnut");
        color_name.put("B95140", "Crail");
        color_name.put("B98D28", "Marigold");
        color_name.put("B9C46A", "Wild Willow");
        color_name.put("B9C8AC", "Rainee");
        color_name.put("BA0101", "Guardsman Red");
        color_name.put("BA450C", "Rock Spray");
        color_name.put("BA6F1E", "Bourbon");
        color_name.put("BA7F03", "Pirate Gold");
        color_name.put("BAB1A2", "Nomad");
        color_name.put("BAC7C9", "Submarine");
        color_name.put("BAEEF9", "Charlotte");
        color_name.put("BB3385", "Medium Red Violet");
        color_name.put("BB8983", "Brandy Rose");
        color_name.put("BBD009", "Rio Grande");
        color_name.put("BBD7C1", "Surf");
        color_name.put("BCC9C2", "Powder Ash");
        color_name.put("BD5E2E", "Tuscany");
        color_name.put("BD978E", "Quicksand");
        color_name.put("BDB1A8", "Silk");
        color_name.put("BDB2A1", "Malta");
        color_name.put("BDB3C7", "Chatelle");
        color_name.put("BDBBD7", "Lavender Gray");
        color_name.put("BDBDC6", "French Gray");
        color_name.put("BDC8B3", "Clay Ash");
        color_name.put("BDC9CE", "Loblolly");
        color_name.put("BDEDFD", "French Pass");
        color_name.put("BEA6C3", "London Hue");
        color_name.put("BEB5B7", "Pink Swan");
        color_name.put("BEDE0D", "Fuego");
        color_name.put("BF5500", "Rose of Sharon");
        color_name.put("BFB8B0", "Tide");
        color_name.put("BFBED8", "Blue Haze");
        color_name.put("BFC1C2", "Silver Sand");
        color_name.put("BFC921", "Key Lime Pie");
        color_name.put("BFDBE2", "Ziggurat");
        color_name.put("BFFF00", "Lime");
        color_name.put("C02B18", "Thunderbird");
        color_name.put("C04737", "Mojo");
        color_name.put("C08081", "Old Rose");
        color_name.put("C0C0C0", "Silver");
        color_name.put("C0D3B9", "Pale Leaf");
        color_name.put("C0D8B6", "Pixie Green");
        color_name.put("C1440E", "Tia Maria");
        color_name.put("C154C1", "Fuchsia Pink");
        color_name.put("C1A004", "Buddha Gold");
        color_name.put("C1B7A4", "Bison Hide");
        color_name.put("C1BAB0", "Tea");
        color_name.put("C1BECD", "Gray Suit");
        color_name.put("C1D7B0", "Sprout");
        color_name.put("C1F07C", "Sulu");
        color_name.put("C26B03", "Indochine");
        color_name.put("C2955D", "Twine");
        color_name.put("C2BDB6", "Cotton Seed");
        color_name.put("C2CAC4", "Pumice");
        color_name.put("C2E8E5", "Jagged Ice");
        color_name.put("C32148", "Maroon Flush");
        color_name.put("C3B091", "Indian Khaki");
        color_name.put("C3BFC1", "Pale Slate");
        color_name.put("C3C3BD", "Gray Nickel");
        color_name.put("C3CDE6", "Periwinkle Gray");
        color_name.put("C3D1D1", "Tiara");
        color_name.put("C3DDF9", "Tropical Blue");
        color_name.put("C41E3A", "Cardinal");
        color_name.put("C45655", "Fuzzy Wuzzy Brown");
        color_name.put("C45719", "Orange Roughy");
        color_name.put("C4C4BC", "Mist Gray");
        color_name.put("C4D0B0", "Coriander");
        color_name.put("C4F4EB", "Mint Tulip");
        color_name.put("C54B8C", "Mulberry");
        color_name.put("C59922", "Nugget");
        color_name.put("C5994B", "Tussock");
        color_name.put("C5DBCA", "Sea Mist");
        color_name.put("C5E17A", "Yellow Green");
        color_name.put("C62D42", "Brick Red");
        color_name.put("C6726B", "Contessa");
        color_name.put("C69191", "Oriental Pink");
        color_name.put("C6A84B", "Roti");
        color_name.put("C6C3B5", "Ash");
        color_name.put("C6C8BD", "Kangaroo");
        color_name.put("C6E610", "Las Palmas");
        color_name.put("C7031E", "Monza");
        color_name.put("C71585", "Red Violet");
        color_name.put("C7BCA2", "Coral Reef");
        color_name.put("C7C1FF", "Melrose");
        color_name.put("C7C4BF", "Cloud");
        color_name.put("C7C9D5", "Ghost");
        color_name.put("C7CD90", "Pine Glade");
        color_name.put("C7DDE5", "Botticelli");
        color_name.put("C88A65", "Antique Brass");
        color_name.put("C8A2C8", "Lilac");
        color_name.put("C8A528", "Hokey Pokey");
        color_name.put("C8AABF", "Lily");
        color_name.put("C8B568", "Laser");
        color_name.put("C8E3D7", "Edgewater");
        color_name.put("C96323", "Piper");
        color_name.put("C99415", "Pizza");
        color_name.put("C9A0DC", "Light Wisteria");
        color_name.put("C9B29B", "Rodeo Dust");
        color_name.put("C9B35B", "Sundance");
        color_name.put("C9B93B", "Earls Green");
        color_name.put("C9C0BB", "Silver Rust");
        color_name.put("C9D9D2", "Conch");
        color_name.put("C9FFA2", "Reef");
        color_name.put("C9FFE5", "Aero Blue");
        color_name.put("CA3435", "Flush Mahogany");
        color_name.put("CABB48", "Turmeric");
        color_name.put("CADCD4", "Paris White");
        color_name.put("CAE00D", "Bitter Lemon");
        color_name.put("CAE6DA", "Skeptic");
        color_name.put("CB8FA9", "Viola");
        color_name.put("CBCAB6", "Foggy Gray");
        color_name.put("CBD3B0", "Green Mist");
        color_name.put("CBDBD6", "Nebula");
        color_name.put("CC3333", "Persian Red");
        color_name.put("CC5500", "Burnt Orange");
        color_name.put("CC7722", "Ochre");
        color_name.put("CC8899", "Puce");
        color_name.put("CCCAA8", "Thistle Green");
        color_name.put("CCCCFF", "Periwinkle");
        color_name.put("CCFF00", "Electric Lime");
        color_name.put("CD5700", "Tenn");
        color_name.put("CD5C5C", "Chestnut Rose");
        color_name.put("CD8429", "Brandy Punch");
        color_name.put("CDF4FF", "Onahau");
        color_name.put("CEB98F", "Sorrell Brown");
        color_name.put("CEBABA", "Cold Turkey");
        color_name.put("CEC291", "Yuma");
        color_name.put("CEC7A7", "Chino");
        color_name.put("CFA39D", "Eunry");
        color_name.put("CFB53B", "Old Gold");
        color_name.put("CFDCCF", "Tasman");
        color_name.put("CFE5D2", "Surf Crest");
        color_name.put("CFF9F3", "Humming Bird");
        color_name.put("CFFAF4", "Scandal");
        color_name.put("D05F04", "Red Stage");
        color_name.put("D06DA1", "Hopbush");
        color_name.put("D07D12", "Meteor");
        color_name.put("D0BEF8", "Perfume");
        color_name.put("D0C0E5", "Prelude");
        color_name.put("D0F0C0", "Tea Green");
        color_name.put("D18F1B", "Geebung");
        color_name.put("D1BEA8", "Vanilla");
        color_name.put("D1C6B4", "Soft Amber");
        color_name.put("D1D2CA", "Celeste");
        color_name.put("D1D2DD", "Mischka");
        color_name.put("D1E231", "Pear");
        color_name.put("D2691E", "Hot Cinnamon");
        color_name.put("D27D46", "Raw Sienna");
        color_name.put("D29EAA", "Careys Pink");
        color_name.put("D2B48C", "Tan");
        color_name.put("D2DA97", "Deco");
        color_name.put("D2F6DE", "Blue Romance");
        color_name.put("D2F8B0", "Gossip");
        color_name.put("D3CBBA", "Sisal");
        color_name.put("D3CDC5", "Swirl");
        color_name.put("D47494", "Charm");
        color_name.put("D4B6AF", "Clam Shell");
        color_name.put("D4BF8D", "Straw");
        color_name.put("D4C4A8", "Akaroa");
        color_name.put("D4CD16", "Bird Flower");
        color_name.put("D4D7D9", "Iron");
        color_name.put("D4DFE2", "Geyser");
        color_name.put("D4E2FC", "Hawkes Blue");
        color_name.put("D54600", "Grenadier");
        color_name.put("D591A4", "Can Can");
        color_name.put("D59A6F", "Whiskey");
        color_name.put("D5D195", "Winter Hazel");
        color_name.put("D5F6E3", "Granny Apple");
        color_name.put("D69188", "My Pink");
        color_name.put("D6C562", "Tacha");
        color_name.put("D6CEF6", "Moon Raker");
        color_name.put("D6D6D1", "Quill Gray");
        color_name.put("D6FFDB", "Snowy Mint");
        color_name.put("D7837F", "New York Pink");
        color_name.put("D7C498", "Pavlova");
        color_name.put("D7D0FF", "Fog");
        color_name.put("D84437", "Valencia");
        color_name.put("D87C63", "Japonica");
        color_name.put("D8BFD8", "Thistle");
        color_name.put("D8C2D5", "Maverick");
        color_name.put("D8FCFA", "Foam");
        color_name.put("D94972", "Cabaret");
        color_name.put("D99376", "Burning Sand");
        color_name.put("D9B99B", "Cameo");
        color_name.put("D9D6CF", "Timberwolf");
        color_name.put("D9DCC1", "Tana");
        color_name.put("D9E4F5", "Link Water");
        color_name.put("D9F7FF", "Mabel");
        color_name.put("DA3287", "Cerise");
        color_name.put("DA5B38", "Flame Pea");
        color_name.put("DA6304", "Bamboo");
        color_name.put("DA6A41", "Red Damask");
        color_name.put("DA70D6", "Orchid");
        color_name.put("DA8A67", "Copperfield");
        color_name.put("DAA520", "Golden Grass");
        color_name.put("DAECD6", "Zanah");
        color_name.put("DAF4F0", "Iceberg");
        color_name.put("DAFAFF", "Oyster Bay");
        color_name.put("DB5079", "Cranberry");
        color_name.put("DB9690", "Petite Orchid");
        color_name.put("DB995E", "Di Serria");
        color_name.put("DBDBDB", "Alto");
        color_name.put("DBFFF8", "Frosted Mint");
        color_name.put("DC143C", "Crimson");
        color_name.put("DC4333", "Punch");
        color_name.put("DCB20C", "Galliano");
        color_name.put("DCB4BC", "Blossom");
        color_name.put("DCD747", "Wattle");
        color_name.put("DCD9D2", "Westar");
        color_name.put("DCDDCC", "Moon Mist");
        color_name.put("DCEDB4", "Caper");
        color_name.put("DCF0EA", "Swans Down");
        color_name.put("DDD6D5", "Swiss Coffee");
        color_name.put("DDF9F1", "White Ice");
        color_name.put("DE3163", "Cerise Red");
        color_name.put("DE6360", "Roman");
        color_name.put("DEA681", "Tumbleweed");
        color_name.put("DEBA13", "Gold Tips");
        color_name.put("DEC196", "Brandy");
        color_name.put("DECBC6", "Wafer");
        color_name.put("DED4A4", "Sapling");
        color_name.put("DED717", "Barberry");
        color_name.put("DEE5C0", "Beryl Green");
        color_name.put("DEF5FF", "Pattens Blue");
        color_name.put("DF73FF", "Heliotrope");
        color_name.put("DFBE6F", "Apache");
        color_name.put("DFCD6F", "Chenin");
        color_name.put("DFCFDB", "Lola");
        color_name.put("DFECDA", "Willow Brook");
        color_name.put("DFFF00", "Chartreuse Yellow");
        color_name.put("E0B0FF", "Mauve");
        color_name.put("E0B646", "Anzac");
        color_name.put("E0B974", "Harvest Gold");
        color_name.put("E0C095", "Calico");
        color_name.put("E0FFFF", "Baby Blue");
        color_name.put("E16865", "Sunglo");
        color_name.put("E1BC64", "Equator");
        color_name.put("E1C0C8", "Pink Flare");
        color_name.put("E1E6D6", "Periglacial Blue");
        color_name.put("E1EAD4", "Kidnapper");
        color_name.put("E1F6E8", "Tara");
        color_name.put("E25465", "Mandy");
        color_name.put("E2725B", "Terracotta");
        color_name.put("E28913", "Golden Bell");
        color_name.put("E292C0", "Shocking");
        color_name.put("E29418", "Dixie");
        color_name.put("E29CD2", "Light Orchid");
        color_name.put("E2D8ED", "Snuff");
        color_name.put("E2EBED", "Mystic");
        color_name.put("E2F3EC", "Apple Green");
        color_name.put("E30B5C", "Razzmatazz");
        color_name.put("E32636", "Alizarin Crimson");
        color_name.put("E34234", "Cinnabar");
        color_name.put("E3BEBE", "Cavern Pink");
        color_name.put("E3F5E1", "Peppermint");
        color_name.put("E3F988", "Mindaro");
        color_name.put("E47698", "Deep Blush");
        color_name.put("E49B0F", "Gamboge");
        color_name.put("E4C2D5", "Melanie");
        color_name.put("E4CFDE", "Twilight");
        color_name.put("E4D1C0", "Bone");
        color_name.put("E4D422", "Sunflower");
        color_name.put("E4D5B7", "Grain Brown");
        color_name.put("E4D69B", "Zombie");
        color_name.put("E4F6E7", "Frostee");
        color_name.put("E4FFD1", "Snow Flurry");
        color_name.put("E52B50", "Amaranth");
        color_name.put("E5841B", "Zest");
        color_name.put("E5CCC9", "Dust Storm");
        color_name.put("E5D7BD", "Stark White");
        color_name.put("E5D8AF", "Hampton");
        color_name.put("E5E0E1", "Bon Jour");
        color_name.put("E5E5E5", "Mercury");
        color_name.put("E5F9F6", "Polar");
        color_name.put("E64E03", "Trinidad");
        color_name.put("E6BE8A", "Gold Sand");
        color_name.put("E6BEA5", "Cashmere");
        color_name.put("E6D7B9", "Double Spanish White");
        color_name.put("E6E4D4", "Satin Linen");
        color_name.put("E6F2EA", "Harp");
        color_name.put("E6F8F3", "Off Green");
        color_name.put("E6FFE9", "Hint of Green");
        color_name.put("E6FFFF", "Tranquil");
        color_name.put("E77200", "Mango Tango");
        color_name.put("E7730A", "Christine");
        color_name.put("E79F8C", "Tonys Pink");
        color_name.put("E79FC4", "Kobi");
        color_name.put("E7BCB4", "Rose Fog");
        color_name.put("E7BF05", "Corn");
        color_name.put("E7CD8C", "Putty");
        color_name.put("E7ECE6", "Gray Nurse");
        color_name.put("E7F8FF", "Lily White");
        color_name.put("E7FEFF", "Bubbles");
        color_name.put("E89928", "Fire Bush");
        color_name.put("E8B9B3", "Shilo");
        color_name.put("E8E0D5", "Pearl Bush");
        color_name.put("E8EBE0", "Green White");
        color_name.put("E8F1D4", "Chrome White");
        color_name.put("E8F2EB", "Gin");
        color_name.put("E8F5F2", "Aqua Squeeze");
        color_name.put("E96E00", "Clementine");
        color_name.put("E97451", "Burnt Sienna");
        color_name.put("E97C07", "Tahiti Gold");
        color_name.put("E9CECD", "Oyster Pink");
        color_name.put("E9D75A", "Confetti");
        color_name.put("E9E3E3", "Ebb");
        color_name.put("E9F8ED", "Ottoman");
        color_name.put("E9FFFD", "Clear Day");
        color_name.put("EA88A8", "Carissma");
        color_name.put("EAAE69", "Porsche");
        color_name.put("EAB33B", "Tulip Tree");
        color_name.put("EAC674", "Rob Roy");
        color_name.put("EADAB8", "Raffia");
        color_name.put("EAE8D4", "White Rock");
        color_name.put("EAF6EE", "Panache");
        color_name.put("EAF6FF", "Solitude");
        color_name.put("EAF9F5", "Aqua Spring");
        color_name.put("EAFFFE", "Dew");
        color_name.put("EB9373", "Apricot");
        color_name.put("EBC2AF", "Zinnwaldite");
        color_name.put("ECA927", "Fuel Yellow");
        color_name.put("ECC54E", "Ronchi");
        color_name.put("ECC7EE", "French Lilac");
        color_name.put("ECCDB9", "Just Right");
        color_name.put("ECE090", "Wild Rice");
        color_name.put("ECEBBD", "Fall Green");
        color_name.put("ECEBCE", "Aths Special");
        color_name.put("ECF245", "Starship");
        color_name.put("ED0A3F", "Red Ribbon");
        color_name.put("ED7A1C", "Tango");
        color_name.put("ED9121", "Carrot Orange");
        color_name.put("ED989E", "Sea Pink");
        color_name.put("EDB381", "Tacao");
        color_name.put("EDC9AF", "Desert Sand");
        color_name.put("EDCDAB", "Pancho");
        color_name.put("EDDCB1", "Chamois");
        color_name.put("EDEA99", "Primrose");
        color_name.put("EDF5DD", "Frost");
        color_name.put("EDF5F5", "Aqua Haze");
        color_name.put("EDF6FF", "Zumthor");
        color_name.put("EDF9F1", "Narvik");
        color_name.put("EDFC84", "Honeysuckle");
        color_name.put("EE82EE", "Lavender Magenta");
        color_name.put("EEC1BE", "Beauty Bush");
        color_name.put("EED794", "Chalky");
        color_name.put("EED9C4", "Almond");
        color_name.put("EEDC82", "Flax");
        color_name.put("EEDEDA", "Bizarre");
        color_name.put("EEE3AD", "Double Colonial White");
        color_name.put("EEEEE8", "Cararra");
        color_name.put("EEEF78", "Manz");
        color_name.put("EEF0C8", "Tahuna Sands");
        color_name.put("EEF0F3", "Athens Gray");
        color_name.put("EEF3C3", "Tusk");
        color_name.put("EEF4DE", "Loafer");
        color_name.put("EEF6F7", "Catskill White");
        color_name.put("EEFDFF", "Twilight Blue");
        color_name.put("EEFF9A", "Jonquil");
        color_name.put("EEFFE2", "Rice Flower");
        color_name.put("EF863F", "Jaffa");
        color_name.put("EFEFEF", "Gallery");
        color_name.put("EFF2F3", "Porcelain");
        color_name.put("F091A9", "Mauvelous");
        color_name.put("F0D52D", "Golden Dream");
        color_name.put("F0DB7D", "Golden Sand");
        color_name.put("F0DC82", "Buff");
        color_name.put("F0E2EC", "Prim");
        color_name.put("F0E68C", "Khaki");
        color_name.put("F0EEFD", "Selago");
        color_name.put("F0EEFF", "Titan White");
        color_name.put("F0F8FF", "Alice Blue");
        color_name.put("F0FCEA", "Feta");
        color_name.put("F18200", "Gold Drop");
        color_name.put("F19BAB", "Wewak");
        color_name.put("F1E788", "Sahara Sand");
        color_name.put("F1E9D2", "Parchment");
        color_name.put("F1E9FF", "Blue Chalk");
        color_name.put("F1EEC1", "Mint Julep");
        color_name.put("F1F1F1", "Seashell");
        color_name.put("F1F7F2", "Saltpan");
        color_name.put("F1FFAD", "Tidal");
        color_name.put("F1FFC8", "Chiffon");
        color_name.put("F2552A", "Flamingo");
        color_name.put("F28500", "Tangerine");
        color_name.put("F2C3B2", "Mandys Pink");
        color_name.put("F2F2F2", "Concrete");
        color_name.put("F2FAFA", "Black Squeeze");
        color_name.put("F34723", "Pomegranate");
        color_name.put("F3AD16", "Buttercup");
        color_name.put("F3D69D", "New Orleans");
        color_name.put("F3D9DF", "Vanilla Ice");
        color_name.put("F3E7BB", "Sidecar");
        color_name.put("F3E9E5", "Dawn Pink");
        color_name.put("F3EDCF", "Wheatfield");
        color_name.put("F3FB62", "Canary");
        color_name.put("F3FBD4", "Orinoco");
        color_name.put("F3FFD8", "Carla");
        color_name.put("F400A1", "Hollywood Cerise");
        color_name.put("F4A460", "Sandy brown");
        color_name.put("F4C430", "Saffron");
        color_name.put("F4D81C", "Ripe Lemon");
        color_name.put("F4EBD3", "Janna");
        color_name.put("F4F2EE", "Pampas");
        color_name.put("F4F4F4", "Wild Sand");
        color_name.put("F4F8FF", "Zircon");
        color_name.put("F57584", "Froly");
        color_name.put("F5C85C", "Cream Can");
        color_name.put("F5C999", "Manhattan");
        color_name.put("F5D5A0", "Maize");
        color_name.put("F5DEB3", "Wheat");
        color_name.put("F5E7A2", "Sandwisp");
        color_name.put("F5E7E2", "Pot Pourri");
        color_name.put("F5E9D3", "Albescent White");
        color_name.put("F5EDEF", "Soft Peach");
        color_name.put("F5F3E5", "Ecru White");
        color_name.put("F5F5DC", "Beige");
        color_name.put("F5FB3D", "Golden Fizz");
        color_name.put("F5FFBE", "Australian Mint");
        color_name.put("F64A8A", "French Rose");
        color_name.put("F653A6", "Brilliant Rose");
        color_name.put("F6A4C9", "Illusion");
        color_name.put("F6F0E6", "Merino");
        color_name.put("F6F7F7", "Black Haze");
        color_name.put("F6FFDC", "Spring Sun");
        color_name.put("F7468A", "Violet Red");
        color_name.put("F77703", "Chilean Fire");
        color_name.put("F77FBE", "Persian Pink");
        color_name.put("F7B668", "Rajah");
        color_name.put("F7C8DA", "Azalea");
        color_name.put("F7DBE6", "We Peep");
        color_name.put("F7F2E1", "Quarter Spanish White");
        color_name.put("F7F5FA", "Whisper");
        color_name.put("F7FAF7", "Snow Drift");
        color_name.put("F8B853", "Casablanca");
        color_name.put("F8C3DF", "Chantilly");
        color_name.put("F8D9E9", "Cherub");
        color_name.put("F8DB9D", "Marzipan");
        color_name.put("F8DD5C", "Energy Yellow");
        color_name.put("F8E4BF", "Givry");
        color_name.put("F8F0E8", "White Linen");
        color_name.put("F8F4FF", "Magnolia");
        color_name.put("F8F6F1", "Spring Wood");
        color_name.put("F8F7DC", "Coconut Cream");
        color_name.put("F8F7FC", "White Lilac");
        color_name.put("F8F8F7", "Desert Storm");
        color_name.put("F8F99C", "Texas");
        color_name.put("F8FACD", "Corn Field");
        color_name.put("F8FDD3", "Mimosa");
        color_name.put("F95A61", "Carnation");
        color_name.put("F9BF58", "Saffron Mango");
        color_name.put("F9E0ED", "Carousel Pink");
        color_name.put("F9E4BC", "Dairy Cream");
        color_name.put("F9E663", "Portica");
        color_name.put("F9EAF3", "Amour");
        color_name.put("F9F8E4", "Rum Swizzle");
        color_name.put("F9FF8B", "Dolly");
        color_name.put("F9FFF6", "Sugar Cane");
        color_name.put("FA7814", "Ecstasy");
        color_name.put("FA9D5A", "Tan Hide");
        color_name.put("FAD3A2", "Corvette");
        color_name.put("FADFAD", "Peach Yellow");
        color_name.put("FAE600", "Turbo");
        color_name.put("FAEAB9", "Astra");
        color_name.put("FAECCC", "Champagne");
        color_name.put("FAF0E6", "Linen");
        color_name.put("FAF3F0", "Fantasy");
        color_name.put("FAF7D6", "Citrine White");
        color_name.put("FAFAFA", "Alabaster");
        color_name.put("FAFDE4", "Hint of Yellow");
        color_name.put("FAFFA4", "Milan");
        color_name.put("FB607F", "Brink Pink");
        color_name.put("FB8989", "Geraldine");
        color_name.put("FBA0E3", "Lavender Rose");
        color_name.put("FBA129", "Sea Buckthorn");
        color_name.put("FBAC13", "Sun");
        color_name.put("FBAED2", "Lavender Pink");
        color_name.put("FBB2A3", "Rose Bud");
        color_name.put("FBBEDA", "Cupid");
        color_name.put("FBCCE7", "Classic Rose");
        color_name.put("FBCEB1", "Apricot Peach");
        color_name.put("FBE7B2", "Banana Mania");
        color_name.put("FBE870", "Marigold Yellow");
        color_name.put("FBE96C", "Festival");
        color_name.put("FBEA8C", "Sweet Corn");
        color_name.put("FBEC5D", "Candy Corn");
        color_name.put("FBF9F9", "Hint of Red");
        color_name.put("FBFFBA", "Shalimar");
        color_name.put("FC0FC0", "Shocking Pink");
        color_name.put("FC80A5", "Tickle Me Pink");
        color_name.put("FC9C1D", "Tree Poppy");
        color_name.put("FCC01E", "Lightning Yellow");
        color_name.put("FCD667", "Goldenrod");
        color_name.put("FCD917", "Candlelight");
        color_name.put("FCDA98", "Cherokee");
        color_name.put("FCF4D0", "Double Pearl Lusta");
        color_name.put("FCF4DC", "Pearl Lusta");
        color_name.put("FCF8F7", "Vista White");
        color_name.put("FCFBF3", "Bianca");
        color_name.put("FCFEDA", "Moon Glow");
        color_name.put("FCFFE7", "China Ivory");
        color_name.put("FCFFF9", "Ceramic");
        color_name.put("FD0E35", "Torch Red");
        color_name.put("FD5B78", "Wild Watermelon");
        color_name.put("FD7B33", "Crusta");
        color_name.put("FD7C07", "Sorbus");
        color_name.put("FD9FA2", "Sweet Pink");
        color_name.put("FDD5B1", "Light Apricot");
        color_name.put("FDD7E4", "Pig Pink");
        color_name.put("FDE1DC", "Cinderella");
        color_name.put("FDE295", "Golden Glow");
        color_name.put("FDE910", "Lemon");
        color_name.put("FDF5E6", "Old Lace");
        color_name.put("FDF6D3", "Half Colonial White");
        color_name.put("FDF7AD", "Drover");
        color_name.put("FDFEB8", "Pale Prim");
        color_name.put("FDFFD5", "Cumulus");
        color_name.put("FE28A2", "Persian Rose");
        color_name.put("FE4C40", "Sunset Orange");
        color_name.put("FE6F5E", "Bittersweet");
        color_name.put("FE9D04", "California");
        color_name.put("FEA904", "Yellow Sea");
        color_name.put("FEBAAD", "Melon");
        color_name.put("FED33C", "Bright Sun");
        color_name.put("FED85D", "Dandelion");
        color_name.put("FEDB8D", "Salomie");
        color_name.put("FEE5AC", "Cape Honey");
        color_name.put("FEEBF3", "Remy");
        color_name.put("FEEFCE", "Oasis");
        color_name.put("FEF0EC", "Bridesmaid");
        color_name.put("FEF2C7", "Beeswax");
        color_name.put("FEF3D8", "Bleach White");
        color_name.put("FEF4CC", "Pipi");
        color_name.put("FEF4DB", "Half Spanish White");
        color_name.put("FEF4F8", "Wisp Pink");
        color_name.put("FEF5F1", "Provincial Pink");
        color_name.put("FEF7DE", "Half Dutch White");
        color_name.put("FEF8E2", "Solitaire");
        color_name.put("FEF8FF", "White Pointer");
        color_name.put("FEF9E3", "Off Yellow");
        color_name.put("FEFCED", "Orange White");
        color_name.put("FF0000", "Red");
        color_name.put("FF007F", "Rose");
        color_name.put("FF00CC", "Purple Pizzazz");
        color_name.put("FF00FF", "Magenta / Fuchsia");
        color_name.put("FF2400", "Scarlet");
        color_name.put("FF3399", "Wild Strawberry");
        color_name.put("FF33CC", "Razzle Dazzle Rose");
        color_name.put("FF355E", "Radical Red");
        color_name.put("FF3F34", "Red Orange");
        color_name.put("FF4040", "Coral Red");
        color_name.put("FF4D00", "Vermilion");
        color_name.put("FF4F00", "International Orange");
        color_name.put("FF6037", "Outrageous Orange");
        color_name.put("FF6600", "Blaze Orange");
        color_name.put("FF66FF", "Pink Flamingo");
        color_name.put("FF681F", "Orange");
        color_name.put("FF69B4", "Hot Pink");
        color_name.put("FF6B53", "Persimmon");
        color_name.put("FF6FFF", "Blush Pink");
        color_name.put("FF7034", "Burning Orange");
        color_name.put("FF7518", "Pumpkin");
        color_name.put("FF7D07", "Flamenco");
        color_name.put("FF7F00", "Flush Orange");
        color_name.put("FF7F50", "Coral");
        color_name.put("FF8C69", "Salmon");
        color_name.put("FF9000", "Pizazz");
        color_name.put("FF910F", "West Side");
        color_name.put("FF91A4", "Pink Salmon");
        color_name.put("FF9933", "Neon Carrot");
        color_name.put("FF9966", "Atomic Tangerine");
        color_name.put("FF9980", "Vivid Tangerine");
        color_name.put("FF9E2C", "Sunshade");
        color_name.put("FFA000", "Orange Peel");
        color_name.put("FFA194", "Mona Lisa");
        color_name.put("FFA500", "Web Orange");
        color_name.put("FFA6C9", "Carnation Pink");
        color_name.put("FFAB81", "Hit Pink");
        color_name.put("FFAE42", "Yellow Orange");
        color_name.put("FFB0AC", "Cornflower Lilac");
        color_name.put("FFB1B3", "Sundown");
        color_name.put("FFB31F", "My Sin");
        color_name.put("FFB555", "Texas Rose");
        color_name.put("FFB7D5", "Cotton Candy");
        color_name.put("FFB97B", "Macaroni and Cheese");
        color_name.put("FFBA00", "Selective Yellow");
        color_name.put("FFBD5F", "Koromiko");
        color_name.put("FFBF00", "Amber");
        color_name.put("FFC0A8", "Wax Flower");
        color_name.put("FFC0CB", "Pink");
        color_name.put("FFC3C0", "Your Pink");
        color_name.put("FFC901", "Supernova");
        color_name.put("FFCBA4", "Flesh");
        color_name.put("FFCC33", "Sunglow");
        color_name.put("FFCC5C", "Golden Tainoi");
        color_name.put("FFCC99", "Peach Orange");
        color_name.put("FFCD8C", "Chardonnay");
        color_name.put("FFD1DC", "Pastel Pink");
        color_name.put("FFD2B7", "Romantic");
        color_name.put("FFD38C", "Grandis");
        color_name.put("FFD700", "Gold");
        color_name.put("FFD800", "School bus Yellow");
        color_name.put("FFD8D9", "Cosmos");
        color_name.put("FFDB58", "Mustard");
        color_name.put("FFDCD6", "Peach Schnapps");
        color_name.put("FFDDAF", "Caramel");
        color_name.put("FFDDCD", "Tuft Bush");
        color_name.put("FFDDCF", "Watusi");
        color_name.put("FFDDF4", "Pink Lace");
        color_name.put("FFDEAD", "Navajo White");
        color_name.put("FFDEB3", "Frangipani");
        color_name.put("FFE1DF", "Pippin");
        color_name.put("FFE1F2", "Pale Rose");
        color_name.put("FFE2C5", "Negroni");
        color_name.put("FFE5A0", "Cream Brulee");
        color_name.put("FFE5B4", "Peach");
        color_name.put("FFE6C7", "Tequila");
        color_name.put("FFE772", "Kournikova");
        color_name.put("FFEAC8", "Sandy Beach");
        color_name.put("FFEAD4", "Karry");
        color_name.put("FFEC13", "Broom");
        color_name.put("FFEDBC", "Colonial White");
        color_name.put("FFEED8", "Derby");
        color_name.put("FFEFA1", "Vis Vis");
        color_name.put("FFEFC1", "Egg White");
        color_name.put("FFEFD5", "Papaya Whip");
        color_name.put("FFEFEC", "Fair Pink");
        color_name.put("FFF0DB", "Peach Cream");
        color_name.put("FFF0F5", "Lavender blush");
        color_name.put("FFF14F", "Gorse");
        color_name.put("FFF1B5", "Buttermilk");
        color_name.put("FFF1D8", "Pink Lady");
        color_name.put("FFF1EE", "Forget Me Not");
        color_name.put("FFF1F9", "Tutu");
        color_name.put("FFF39D", "Picasso");
        color_name.put("FFF3F1", "Chardon");
        color_name.put("FFF46E", "Paris Daisy");
        color_name.put("FFF4CE", "Barley White");
        color_name.put("FFF4DD", "Egg Sour");
        color_name.put("FFF4E0", "Sazerac");
        color_name.put("FFF4E8", "Serenade");
        color_name.put("FFF4F3", "Chablis");
        color_name.put("FFF5EE", "Seashell Peach");
        color_name.put("FFF5F3", "Sauvignon");
        color_name.put("FFF6D4", "Milk Punch");
        color_name.put("FFF6DF", "Varden");
        color_name.put("FFF6F5", "Rose White");
        color_name.put("FFF8D1", "Baja White");
        color_name.put("FFF9E2", "Gin Fizz");
        color_name.put("FFF9E6", "Early Dawn");
        color_name.put("FFFAF4", "Bridal Heath");
        color_name.put("FFFBDC", "Scotch Mist");
        color_name.put("FFFBF9", "Soapstone");
        color_name.put("FFFC99", "Witch Haze");
        color_name.put("FFFCEA", "Buttery White");
        color_name.put("FFFCEE", "Island Spice");
        color_name.put("FFFDD0", "Cream");
        color_name.put("FFFDE6", "Chilean Heath");
        color_name.put("FFFDE8", "Travertine");
        color_name.put("FFFDF3", "Orchid White");
        color_name.put("FFFDF4", "Quarter Pearl Lusta");
        color_name.put("FFFEE1", "Half and Half");
        color_name.put("FFFEEC", "Apricot White");
        color_name.put("FFFEF0", "Rice Cake");
        color_name.put("FFFEF6", "Black White");
        color_name.put("FFFEFD", "Romance");
        color_name.put("FFFF66", "Laser Lemon");
        color_name.put("FFFF99", "Pale Canary");
        color_name.put("FFFFB4", "Portafino");
        color_name.put("FFFFF0", "Ivory");
        color_name.put("FFFFFF", "White");
        String name;
        if (color_name.containsKey(hexCode)) {
            name = color_name.get(hexCode);
        } else {
            name = nearestColor(hexCode, color_name);
        }
        return name;
    }
}
