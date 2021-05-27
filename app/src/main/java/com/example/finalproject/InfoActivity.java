package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class InfoActivity extends AppCompatActivity {

    private Button btnSkip, btnNext;
    private ViewPager sViewPager;
    private LinearLayout dotsLayout;
    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_slider1);
        initView();
        sliderAdapter = new SliderAdapter(this);
        sViewPager.setAdapter(sliderAdapter);
        sViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        addBottomDots(0);
    }

    public  void btnSkipClick(View v) {
        launchHomeScreen();
    }

    public  void btnNextClick(View v) {
        int current = getItem();
        if (current < sliderAdapter.image_slide.length) {
            sViewPager.setCurrentItem(current);
        } else {
            launchHomeScreen();
        }
    }

    private void launchHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
        if (ContextCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(InfoActivity.this,new String[] { Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE },0);
        }
        else if (ContextCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(InfoActivity.this,new String[] { Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE },1);
        }
        finish();
    }

    private int getItem() {
        return sViewPager.getCurrentItem() + 1;
    }

    private void initView() {
        sViewPager = findViewById(R.id.sViewPager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == sliderAdapter.image_slide.length - 1) {
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    // set of Dots points
    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[2];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colortWhite));
            dotsLayout.addView(dots[i]);
        }
        dots[currentPage].setTextColor(getResources().getColor(R.color.colorAccent));
    }
}


