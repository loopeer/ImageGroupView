package com.loopeer.android.apps.imagegroupview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

public class SquareImageView extends SimpleDraweeView implements View.OnClickListener{

    private String mLocalUrl;
    private String mUploadKey;
    private String mInternetUrl;
    private Context mContext;
    private boolean mClickUpload = true;

    private int parentLeftMargin;
    private int parentRightMargin;

    public SquareImageView(Context context) {
        this(context, null);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        setScaleType(ImageView.ScaleType.CENTER_CROP);
        setClickable(mClickUpload);
        setOnClickListener(this);
        GenericDraweeHierarchyBuilder builder1 = new GenericDraweeHierarchyBuilder(getContext().getResources());
        builder1.setPlaceholderImage(getContext().getResources().getDrawable(R.drawable.ic_image_default), ScalingUtils.ScaleType.CENTER_CROP);
        getControllerBuilder().build().setHierarchy(builder1.build());
    }

    public void setAlterParentMargin(int left, int right) {
        parentLeftMargin += left;
        parentRightMargin += right;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int screenWidth = getResources().getDisplayMetrics().widthPixels;
        final int margin = 12;
        int imageViewWidth = (screenWidth - parentLeftMargin - parentRightMargin - margin * 2) / 3;
        setMeasuredDimension(imageViewWidth, imageViewWidth);
    }

    public void setLocalUrl(String localUrl) {
        if (!TextUtils.isEmpty(mInternetUrl)) mInternetUrl = null;
        mLocalUrl = localUrl;

        ImageDisplayHelper.displayImageLocal(this, mLocalUrl, 200, 200);
    }

    public void setmInternetUrl(String internetUrl) {
        mInternetUrl = internetUrl;
    }

    public void setUploadKey(String key) {
        mUploadKey = key;
    }

    public String getUploadImageKey() {
        return mUploadKey;
    }

    public String getLocalUrl() {
        return mLocalUrl;
    }
    public String getImageLocalUrl() {
        return mLocalUrl;
    }

    public String getInternetUrl() {
        return mInternetUrl;
    }

    public void setInternetData(String netUrl) {
        mInternetUrl = netUrl;
        mLocalUrl = null;
        if (netUrl == null) {
            this.setImageResource(R.drawable.ic_image_default);
            return;
        }
        ImageDisplayHelper.displayImage(this, mInternetUrl, R.drawable.ic_image_default , 200, 200);
    }

    public void setClickAble (boolean able) {
        mClickUpload = able;
    }

    @Override
    public void onClick(View v) {
        if (mClickUpload) {

        } else {

        }
    }
}
