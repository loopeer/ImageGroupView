package com.loopeer.android.librarys.imagegroupview.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.OnTabOneClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.ImagesSwitcherAdapter;
import com.loopeer.android.librarys.imagegroupview.fragment.ScaleImageFragment;
import com.loopeer.android.librarys.imagegroupview.model.ImageSwitcherWrapper;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;
import com.loopeer.android.librarys.imagegroupview.view.ElasticDragDismissFrameLayout;
import com.loopeer.android.librarys.imagegroupview.view.MutipleTouchViewPager;

import java.util.ArrayList;


public class ImageSwitcherActivity extends AppCompatActivity implements OnTabOneClickListener {

    private MutipleTouchViewPager pager;
    private ArrayList<ImageSwitcherWrapper> mImageSwitcherWrappers;
    private ImagesSwitcherAdapter mAdapter;
    private boolean canImageDelete;
    private int mCurrentPagerPosition;
    private int placeholderDrawable;
    private ArrayList<Integer> mDeletePositions;
    private ImageView btnDelete;
    private int mImageGroupId;
    private ElasticDragDismissFrameLayout mDragDismissFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_switcher);
        mDragDismissFrameLayout = (ElasticDragDismissFrameLayout) findViewById(R.id.drag_frame);

        mDeletePositions = new ArrayList<>();
        parseIntent();
        updateView();
        updateData();
        mDragDismissFrameLayout.addListener(new ElasticDragDismissFrameLayout.ElasticDragDismissCallback() {
            @Override
            public void onDragDismissed() {
                super.onDragDismissed();
                ImageSwitcherActivity.this.finish();
            }
        });
        mDragDismissFrameLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mDragDismissFrameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        doAnimation(true);
                    }
                });
    }

    private void doAnimation(final boolean isEnter) {
        ScaleImageFragment imageFragment = mAdapter.getFragmentByPosition(mCurrentPagerPosition);
        View scaleView = imageFragment.getViewScaleView();
        View containerScrollView = imageFragment.getContainerScrollView();
        View placeholderView = imageFragment.getPlaceholderView();

        if (imageFragment.getImageSuccess()) {
            placeholderView.setVisibility(View.INVISIBLE);
        }
        float width = imageFragment.getImageWidth();
        float height = imageFragment.getImageHeight();
        int blackColor = ContextCompat.getColor(this, R.color.scale_bg_black);
        int whiteColor = ContextCompat.getColor(this, R.color.scale_bg_white);
        float[] scaleX, scaleY, translationX, translationY;
        int[] bgColor;
        SquareImage squareImage = mImageSwitcherWrappers.get(mCurrentPagerPosition).squareImage;
        float distanceX = squareImage.getCenterX() - DisplayUtils.getScreenWidth(this) / 2;
        float distanceY = squareImage.getCenterY() - (DisplayUtils.getScreenHeight(this) - DisplayUtils.getStatusBarHeight(this)) / 2;
        if (isEnter) {
            scaleView.setScaleX((float) squareImage.width / width);
            scaleView.setScaleY((float) squareImage.height / height);
            scaleView.setTranslationX(distanceX);
            scaleView.setTranslationY(distanceY);
            scaleX = new float[]{scaleView.getScaleX(), 1};
            scaleY = new float[]{scaleView.getScaleY(), 1};
            translationX = new float[]{distanceX, 0};
            translationY = new float[]{distanceY, 0};
            bgColor = new int[]{whiteColor, blackColor};
        } else {
            scaleX = new float[]{1, (float) squareImage.width / width};
            scaleY = new float[]{1, (float) squareImage.height / height};
            translationX = new float[]{0, distanceX};
            translationY = new float[]{0, distanceY};
            bgColor = new int[]{blackColor, whiteColor};
        }
        ObjectAnimator sX = ObjectAnimator.ofFloat(scaleView, View.SCALE_X, scaleX);
        ObjectAnimator sY = ObjectAnimator.ofFloat(scaleView, View.SCALE_Y, scaleY);
        ObjectAnimator tX = ObjectAnimator.ofFloat(scaleView, View.TRANSLATION_X, translationX);
        ObjectAnimator tY = ObjectAnimator.ofFloat(scaleView, View.TRANSLATION_Y, translationY);
        ObjectAnimator bg = ObjectAnimator.ofInt(containerScrollView, "backgroundColor", bgColor);
        bg.setEvaluator(new ArgbEvaluator());
        bg.setDuration(125);

        AnimatorSet animatorSet = new AnimatorSet();
        if (isEnter) {
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        } else {
            animatorSet.setInterpolator(new AccelerateInterpolator());
        }
        animatorSet.setDuration(250);
        animatorSet.play(sX).with(sY).with(tX).with(tY);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isEnter) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });
        bg.start();
        animatorSet.start();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        ArrayList<SquareImage> images = intent.getParcelableArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL);
        setUpImageWrappers(images);
        mImageGroupId = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, 0);
        mCurrentPagerPosition = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION, 0);
        canImageDelete = intent.getBooleanExtra(NavigatorImage.EXTRA_IMAGE_DELETE, false);
        placeholderDrawable = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_PLACE_DRAWABLE_ID, R.drawable.ic_image_default);
    }

    private void setUpImageWrappers(ArrayList<SquareImage> images) {
        mImageSwitcherWrappers = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            mImageSwitcherWrappers.add(new ImageSwitcherWrapper(images.get(i), i));
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
        mAdapter = new ImagesSwitcherAdapter(getSupportFragmentManager(), placeholderDrawable);
        mAdapter.setOnTabOneClickListener(this);
        pager.setAdapter(mAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPagerPosition = position;
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
        for (ImageSwitcherWrapper imageSwitcherWrapper : mImageSwitcherWrappers) {
            squareImages.add(imageSwitcherWrapper.squareImage);
        }
        return squareImages;
    }

    private void setCurrentPosition() {
        pager.setCurrentItem(mCurrentPagerPosition);
    }

    private void deleteImage() {
        mDeletePositions.add(mImageSwitcherWrappers.get(mCurrentPagerPosition).originPosition);
        updateImagesPosition();
        mImageSwitcherWrappers.remove(mCurrentPagerPosition);
        if (mImageSwitcherWrappers.size() == 0) {
            goBackWithResult(false);
        } else if (mCurrentPagerPosition == mImageSwitcherWrappers.size()) {
            mCurrentPagerPosition = mCurrentPagerPosition - 1;
            updateData();
        } else {
            updateData();
        }
    }

    private void updateImagesPosition() {
        if (mCurrentPagerPosition == mImageSwitcherWrappers.size() - 1) {
            return;
        }
        for (int i = mCurrentPagerPosition; i < mImageSwitcherWrappers.size() - 1; i++) {
            SquareImage squareImage = mImageSwitcherWrappers.get(i).squareImage;
            mImageSwitcherWrappers.get(i + 1).squareImage.setPosition(
                    squareImage.left, squareImage.top, squareImage.width, squareImage.height);
        }
    }

    @Override
    public void onBackPressed() {
        goBackWithResult(true);
    }

    private void goBackWithResult(boolean doAnimation) {
        Intent intent = new Intent();
        intent.putExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION, mDeletePositions);
        intent.putExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, mImageGroupId);
        setResult(Activity.RESULT_OK, intent);
        if (doAnimation) {
            doAnimation(false);
        } else {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onTabOneClick() {
        goBackWithResult(true);
    }
}
