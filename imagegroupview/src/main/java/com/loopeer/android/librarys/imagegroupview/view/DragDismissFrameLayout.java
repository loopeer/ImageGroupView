package com.loopeer.android.librarys.imagegroupview.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.activity.ImageSwitcherActivity;
import com.loopeer.android.librarys.imagegroupview.photodraweeview.PhotoDraweeView;
import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;

public class DragDismissFrameLayout extends FrameLayout {

    private PhotoDraweeView mPhotoDraweeView;
    private int mScreenHeight;
    private int mDragDismissDistance;
    private int mBlackColor;
    private int mWhiteColor;
    private boolean mIsMoving;

    public DragDismissFrameLayout(Context context) {
        this(context, null);
    }

    public DragDismissFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragDismissFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScreenHeight = DisplayUtils.getScreenHeight(getContext());
        mDragDismissDistance = mScreenHeight / 8;
        mBlackColor = ContextCompat.getColor(getContext(), R.color.scale_bg_black);
        mWhiteColor = ContextCompat.getColor(getContext(), R.color.scale_bg_white);
    }

    public void setPhotoDraweeView(PhotoDraweeView photoDraweeView) {
        mPhotoDraweeView = photoDraweeView;
    }

    float downX, downY, moveX, moveY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mPhotoDraweeView == null) {
            return super.dispatchTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                if (!mIsMoving && mPhotoDraweeView.getScale() == mPhotoDraweeView.getMinimumScale()
                        && moveY > downY && Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
                    mIsMoving = true;
                }

                if (mIsMoving) {
                    mPhotoDraweeView.setTranslationX(moveX - downX);
                    mPhotoDraweeView.setTranslationY(moveY - downY);
                    float scale = calculateScale(moveY - downY);
                    mPhotoDraweeView.setScaleX(scale);
                    mPhotoDraweeView.setScaleY(scale);
                    setBackgroundColor(calculateColor(calculateColorFraction(moveY - downY), mBlackColor, mWhiteColor));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsMoving) {
                    moveTouchUp();
                }
                mIsMoving = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void notMoveTouchUp() {
        if (mPhotoDraweeView.getScaleX() == 1.0f && mPhotoDraweeView.getScaleY() == 1.0f
                && mPhotoDraweeView.getTranslationX() == 0.0f && mPhotoDraweeView.getTranslationY() == 0.0f) {
            //消失
            ((ImageSwitcherActivity) getContext()).onTabOneClick();
        }
    }

    private void moveTouchUp() {
        if (mPhotoDraweeView.getTranslationY() >= mDragDismissDistance) {
            //消失
            ((ImageSwitcherActivity) getContext()).onTabOneClick();
        } else {
            //复位
            doRestoreAnimation();
        }
    }

    private void doRestoreAnimation() {
        setBackgroundColor(mBlackColor);
        mPhotoDraweeView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationX(0.0f)
                .translationY(0.0f)
                .setDuration(100)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }

    private float calculateScale(float distance) {
        if (mPhotoDraweeView.getTranslationY() <= 0.0f) {
            return 1.0f;
        }
        return 1.0f - Math.abs(distance) / mScreenHeight;
    }

    private float calculateColorFraction(float distance) {
        if (mPhotoDraweeView.getTranslationY() <= 0.0f) {
            return 0.0f;
        }
        return Math.abs(distance) / mScreenHeight;
    }

    public int calculateColor(float fraction, int startColor, int endColor) {
        int startA = (startColor >> 24) & 0xff;
        int startR = (startColor >> 16) & 0xff;
        int startG = (startColor >> 8) & 0xff;
        int startB = startColor & 0xff;

        int endA = (endColor >> 24) & 0xff;
        int endR = (endColor >> 16) & 0xff;
        int endG = (endColor >> 8) & 0xff;
        int endB = endColor & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24 |
                (startR + (int) (fraction * (endR - startR))) << 16 |
                (startG + (int) (fraction * (endG - startG))) << 8 |
                (startB + (int) (fraction * (endB - startB)));
    }
}
