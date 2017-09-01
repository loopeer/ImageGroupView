package com.loopeer.android.librarys.imagegroupview.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.OnTabOneClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.ImagesSwitcherAdapter;
import com.loopeer.android.librarys.imagegroupview.fragment.ScaleImageFragment;
import com.loopeer.android.librarys.imagegroupview.model.ImageSwitcherWrapper;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.photodraweeview.PhotoDraweeView;
import com.loopeer.android.librarys.imagegroupview.view.DragDismissFrameLayout;
import com.loopeer.android.librarys.imagegroupview.view.MutipleTouchViewPager;

import java.util.ArrayList;


public class ImageSwitcherActivity extends AppCompatActivity implements OnTabOneClickListener {

    private MutipleTouchViewPager mViewPager;
    private ArrayList<ImageSwitcherWrapper> mImageSwitcherWrappers;
    private ImagesSwitcherAdapter mAdapter;
    private boolean canImageDelete;
    private int mCurrentPagerPosition;
    private int placeholderDrawable;
    private ArrayList<Integer> mDeletePositions;
    private ImageView mBtnDelete;
    private int mImageGroupId;
    private FrameLayout mDragDismissFrameLayout;
    private boolean mDragDismiss;
    private FrameLayout mDragLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_switcher);
        mDragDismissFrameLayout = (FrameLayout) findViewById(R.id.drag_frame);

        mDeletePositions = new ArrayList<>();
        parseIntent();
        updateView();
        updateData();

        mDragDismissFrameLayout.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mDragDismissFrameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        doAnimation(true);
                    }
                });
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
//        }
//    }

    private void doAnimation(final boolean isEnter) {
        ScaleImageFragment imageFragment = mAdapter.getFragmentByPosition(mCurrentPagerPosition);//应该是滑动时显示图片的那个页面
        PhotoDraweeView photoDraweeView = (PhotoDraweeView) imageFragment.getScaleImage();
        DragDismissFrameLayout dismissFrameLayout = (DragDismissFrameLayout) imageFragment.getDismissFrameLayout();//imageFragment中负责消失动画的frameLayout
        dismissFrameLayout.setDragDismiss(mDragDismiss);

        float width = photoDraweeView.getImageWidth();
        float height = photoDraweeView.getImageHeight();
        int blackColor = ContextCompat.getColor(this, R.color.scale_bg_black);
        int whiteColor = ContextCompat.getColor(this, R.color.scale_bg_white);
        float[] scaleX, scaleY, translationX, translationY;
        int[] bgColor;
        SquareImage squareImage = mImageSwitcherWrappers.get(mCurrentPagerPosition).squareImage;
        float distanceX = squareImage.getCenterX() - photoDraweeView.getCenterX();
        float distanceY = squareImage.getCenterY() - photoDraweeView.getCenterY();
        if (isEnter) {//进入
            if (squareImage.width >= squareImage.height) {
                photoDraweeView.setScaleX((float) squareImage.width / width);
                photoDraweeView.setScaleY((float) squareImage.height / width);
            } else {
                photoDraweeView.setScaleX((float) squareImage.width / height);
                photoDraweeView.setScaleY((float) squareImage.height / height);
            }

            photoDraweeView.setTranslationX(distanceX);
            photoDraweeView.setTranslationY(distanceY);
            scaleX = new float[]{photoDraweeView.getScaleX(), 1};
            scaleY = new float[]{photoDraweeView.getScaleY(), 1};
            translationX = new float[]{distanceX, 0};
            translationY = new float[]{distanceY, 0};
            bgColor = new int[]{whiteColor, blackColor};
        } else {//退出
            if (squareImage.width >= squareImage.height) {
                scaleX = new float[]{photoDraweeView.getScaleX(), (float) squareImage.width / width};
                scaleY = new float[]{photoDraweeView.getScaleY(), (float) squareImage.height / width};
            } else {
                scaleX = new float[]{photoDraweeView.getScaleX(), (float) squareImage.width / height};
                scaleY = new float[]{photoDraweeView.getScaleY(), (float) squareImage.height / height};
            }

            translationX = new float[]{photoDraweeView.getTranslationX(), distanceX};
            translationY = new float[]{photoDraweeView.getTranslationY(), distanceY};
            bgColor = new int[]{((ColorDrawable) dismissFrameLayout.getBackground()).getColor(), whiteColor};
        }
        ObjectAnimator sX = ObjectAnimator.ofFloat(photoDraweeView, View.SCALE_X, scaleX);
        ObjectAnimator sY = ObjectAnimator.ofFloat(photoDraweeView, View.SCALE_Y, scaleY);
        ObjectAnimator tX = ObjectAnimator.ofFloat(photoDraweeView, View.TRANSLATION_X, translationX);
        ObjectAnimator tY = ObjectAnimator.ofFloat(photoDraweeView, View.TRANSLATION_Y, translationY);
        ObjectAnimator bg = ObjectAnimator.ofInt(dismissFrameLayout, "backgroundColor", bgColor);
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
        mDragDismiss = intent.getBooleanExtra(NavigatorImage.EXTRA_DRAG_DISMISS, true);
    }

    private void setUpImageWrappers(ArrayList<SquareImage> images) {
        mImageSwitcherWrappers = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            mImageSwitcherWrappers.add(new ImageSwitcherWrapper(images.get(i), i));
        }
    }

    private void updateView() {
        mViewPager = (MutipleTouchViewPager) findViewById(R.id.view_pager);
        mBtnDelete = (ImageView) findViewById(R.id.btn_delete);
        mDragLayout = (FrameLayout) findViewById(R.id.drag_frame);
        mBtnDelete.setVisibility(canImageDelete ? View.VISIBLE : View.GONE);
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
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
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.view_pager_page_margin));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPagerPosition = position;
                updatePositionText();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mDragLayout.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    mDragLayout.setBackgroundResource(R.color.scale_bg_black);
                }
            }
        });
    }

    protected void updatePositionText() {
        ((TextView) findViewById(R.id.text_bottom)).setText((mCurrentPagerPosition + 1) + "/" + mImageSwitcherWrappers.size());
    }

    private void updateData() {
        mAdapter.setImages(createShowImages());
        setCurrentPosition();
        updatePositionText();
    }

    private ArrayList<SquareImage> createShowImages() {
        ArrayList<SquareImage> squareImages = new ArrayList<>();
        for (ImageSwitcherWrapper imageSwitcherWrapper : mImageSwitcherWrappers) {
            squareImages.add(imageSwitcherWrapper.squareImage);
        }
        return squareImages;
    }

    private void setCurrentPosition() {
        mViewPager.setCurrentItem(mCurrentPagerPosition);
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
