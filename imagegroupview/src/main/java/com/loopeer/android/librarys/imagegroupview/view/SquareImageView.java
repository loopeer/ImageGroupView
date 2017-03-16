package com.loopeer.android.librarys.imagegroupview.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

public class SquareImageView extends SimpleDraweeView implements View.OnClickListener{

    private String mLocalUrl;
    private String mUploadKey;
    private String mInternetUrl;
    private int placeholderDrawable;
    private int width;
    private boolean mClickUpload = true;

    public SquareImageView(Context context) {
        this(context, null);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        placeholderDrawable = R.drawable.ic_image_default;
        setScaleType(ScaleType.CENTER_CROP);
        setClickable(mClickUpload);
        setOnClickListener(this);
        GenericDraweeHierarchyBuilder builder1 = new GenericDraweeHierarchyBuilder(getContext().getResources());
        builder1.setPlaceholderImage(ContextCompat.getDrawable(getContext(), placeholderDrawable), ScalingUtils.ScaleType.CENTER_CROP);
        getControllerBuilder().build().setHierarchy(builder1.build());
    }

    public void setWidthByParent(int widthByParent) {
        width = widthByParent;
    }

    public void setPlaceholderDrawable(int src) {
        placeholderDrawable = src;
        getHierarchy().setPlaceholderImage(placeholderDrawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setLocalUrl(final String localUrl) {
        if (!TextUtils.isEmpty(mInternetUrl)) mInternetUrl = null;
        if (getHeight() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setLocalUrl(localUrl);
                }
            });
            return;
        }
        mLocalUrl = localUrl;
        ImageGroupDisplayHelper.displayImageLocal(this, mLocalUrl, getWidth(), getHeight());
    }

    @SuppressWarnings("unused")
    public void setmInternetUrl(String internetUrl) {
        mInternetUrl = internetUrl;
    }

    @SuppressWarnings("unused")
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

    public void setInternetData(final String netUrl) {
        mInternetUrl = netUrl;
        mLocalUrl = null;
        if (netUrl == null) {
            setImageResource(placeholderDrawable);
            return;
        }
        if (getHeight() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setInternetData(netUrl);
                }
            });
            return;
        }
        ImageGroupDisplayHelper.displayImage(this, mInternetUrl, placeholderDrawable, getWidth(), getHeight());
    }

    public void setImageData(SquareImage squareImage) {
        if (squareImage.localUrl != null) setLocalUrl(squareImage.localUrl);
        if (squareImage.interNetUrl != null) setInternetData(squareImage.interNetUrl);
        if (squareImage.urlKey != null) setUploadKey(squareImage.urlKey);
    }

    @SuppressWarnings("unused")
    public void setClickAble (boolean able) {
        mClickUpload = able;
    }

    @Override
    public void onClick(View v) {
        if (mClickUpload) {

        } else {

        }
    }

    public View getSquareImage() {
        return null;
    }

    public void setRoundAsCircle(boolean flag) {
        if (flag == false) return;
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(flag);
        getHierarchy().setRoundingParams(roundingParams);

    }
}
