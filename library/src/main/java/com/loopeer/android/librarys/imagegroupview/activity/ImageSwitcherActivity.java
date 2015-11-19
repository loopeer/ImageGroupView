package com.loopeer.android.librarys.imagegroupview.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.loopeer.android.librarys.imagegroupview.model.ImageSwitcherWrapper;
import com.loopeer.android.librarys.imagegroupview.adapter.ImagesSwitcherAdapter;
import com.loopeer.android.librarys.imagegroupview.MutipleTouchViewPager;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.OnTabOneClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

import java.util.ArrayList;


public class ImageSwitcherActivity extends AppCompatActivity implements OnTabOneClickListener {

    private MutipleTouchViewPager pager;
    private ArrayList<ImageSwitcherWrapper> imageSwitcherWrappers;
    private ImagesSwitcherAdapter mAdapter;
    private boolean canImageDelete;
    private int currentPagerPosition;
    private ArrayList<Integer> deletePositions;
    private ImageView btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_switcher);

        deletePositions = new ArrayList<>();
        parseIntent();
        updateView();
        updateData();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        ArrayList<SquareImage> images = intent.getParcelableArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL);
        setUpImageWrappers(images);
        currentPagerPosition = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION, 0);
        canImageDelete = intent.getBooleanExtra(NavigatorImage.EXTRA_IMAGE_DELETE, false);
    }

    private void setUpImageWrappers(ArrayList<SquareImage> images) {
        imageSwitcherWrappers = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
           imageSwitcherWrappers.add(new ImageSwitcherWrapper(images.get(i), i));
        }
    }

    private void updateView() {
        pager = (MutipleTouchViewPager) findViewById(R.id.view_pager);
        btnDelete = (ImageView) findViewById(R.id.btn_delete);
        btnDelete.setVisibility(canImageDelete ? View.VISIBLE : View.GONE);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });
        setUpView();
    }

    private void setUpView() {
        mAdapter = new ImagesSwitcherAdapter(getSupportFragmentManager());
        mAdapter.setOnTabOneClickListener(this);
        pager.setAdapter(mAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updateData() {
        mAdapter.setImages(createShowImages());
        setCurrentPosition();
    }

    private ArrayList<SquareImage> createShowImages() {
        ArrayList<SquareImage> squareImages = new ArrayList<>();
        for (ImageSwitcherWrapper imageSwitcherWrapper : imageSwitcherWrappers) {
            squareImages.add(imageSwitcherWrapper.squareImage);
        }
        return squareImages;
    }

    private void setCurrentPosition() {
        pager.setCurrentItem(currentPagerPosition);
    }

    private void deleteImage() {
        deletePositions.add(imageSwitcherWrappers.get(currentPagerPosition).originPosition);
        imageSwitcherWrappers.remove(currentPagerPosition);
        if (imageSwitcherWrappers.size() == 0) {
            goBackWithResult();
        } else if (currentPagerPosition == imageSwitcherWrappers.size()) {
            currentPagerPosition = currentPagerPosition - 1;
            updateData();
        } else {
            updateData();
        }
    }

    @Override
    public void onBackPressed() {
        goBackWithResult();
    }

    private void goBackWithResult() {
        Intent intent = new Intent();
        intent.putExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION, deletePositions);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onTabOneClick() {
        goBackWithResult();
    }
}
