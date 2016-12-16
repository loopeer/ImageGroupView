package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;

public class ScaleImageView extends SimpleDraweeView {

    private Matrix mMatrix;

    private static final float ZOOM_SCALE = 0.2f;
    private static final float REDUCE_SCALE = 0.0f;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.concat(mMatrix);
        super.onDraw(canvas);
    }

    public void setScale(float scale) {
        mMatrix.setScale(1 + scale, 1 + scale, getMeasuredWidth() / 2, getMeasuredWidth() / 2);
        invalidate();
    }

    public static float getZoomScale() {
        return ZOOM_SCALE;
    }

    public static float getReduceScale() {
        return REDUCE_SCALE;
    }
}
