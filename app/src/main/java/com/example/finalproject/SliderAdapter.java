package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {

    private final Context context;

//    private TextView slideHeading, slideDescription;
//    private ImageView slide_imageView;


    public SliderAdapter(Context context) {

        this.context = context;
    }

    // img Array
    public int[] image_slide ={
//            R.drawable.ic_baseline_photo_camera_30,
//            R.drawable.ic_baseline_photo_library_30,
//            R.drawable.ic_baseline_image_search_30
//            R.drawable.ic_baseline_photo_camera_30,
//            R.drawable.ic_baseline_photo_library_30,
//            R.drawable.ic_baseline_image_search_30,
//            R.drawable.ic_baseline_photo_library_30,
            R.drawable.color,
            R.drawable.ic_baseline_image_search_30
    };

    // heading Array
    public String[] heading_slide ={
//            "CAMERA",
//            "GALLERY",
//            "FIND COLOR\nBY CAMERA",
//            "FIND COLOR\nBY GALLERY",
//            "FIND OBJECT\nBY CAMERA",
//            "FIND OBJECT\nBY GALLERY"
            "FIND COLOR",
            "FIND OBJECT"
    };

    // description Array
    public String[] description_slide ={
//            "Pick color from camera\nand Realtime find object",
//            "Pick color from gallery",
//            "Detect realtime color and find it"
//            "Pick color via camera",
//            "Pick color via gallery",
//            "Realtime find object\nvia camera",
//            "Detect color to find object by gallery"
            "Pick color via camera\nPick color via gallery",
            "Realtime find object\nvia camera\nDetect color to find object\nvia gallery",
    };




    @Override
    public int getCount() {
        return heading_slide.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container,false);
        container.addView(view);

        ImageView slide_imageView = view.findViewById(R.id.imageView1);
        TextView slideHeading = view.findViewById(R.id.tvHeading);
        TextView  slideDescription = view.findViewById(R.id.tvDescription);

        slide_imageView.setImageResource(image_slide[position]);
        slideHeading.setText(heading_slide[position]);
        slideDescription.setText(description_slide[position]);

        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }

//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        View view = (View) object;
//        container.removeView(view);
//    }

}


