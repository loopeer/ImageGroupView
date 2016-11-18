package com.loopeer.android.librarys.imagegroupview.utils;

import android.graphics.Matrix;
import android.graphics.Rect;

import com.facebook.drawee.drawable.ScalingUtils;

import java.util.HashMap;
import java.util.Map;

public class AnimatorScaleType implements ScalingUtils.ScaleType {

    public static AnimatorScaleType INSTANCE = new AnimatorScaleType();

    private static final String ZOOM = "zoom";
    private static final String REDUCE = "reduce";
    public static final float ZOOM_SCALE = 0.2f;
    public static final float REDUCE_SCALE = 0.0f;
    private static Map<String, AnimatorScaleType> POOL_INSTANCE = new HashMap<>();

    static {
        if (!POOL_INSTANCE.containsKey(ZOOM)) {
            POOL_INSTANCE.put(ZOOM, new AnimatorScaleType(ZOOM_SCALE));
        }
        if (!POOL_INSTANCE.containsKey(REDUCE)) {
            POOL_INSTANCE.put(REDUCE, new AnimatorScaleType(REDUCE_SCALE));
        }
    }

    public static AnimatorScaleType getZoomScaleType() {
        return POOL_INSTANCE.get(ZOOM);
    }

    public static AnimatorScaleType getReduceScaleType() {
        return POOL_INSTANCE.get(REDUCE);
    }

    private AnimatorScaleType() {
    }

    private AnimatorScaleType(float scale) {
        mScale = scale;
    }

    private float mScale;

    public void setScale(float scale) {
        mScale = scale;
    }

    @Override
    public Matrix getTransform(Matrix outTransform, Rect parentRect, int childWidth, int childHeight, float focusX, float focusY) {
        float scale, dx, dy;
        final float scaleX = (float) parentRect.width() / (float) childWidth;
        final float scaleY = (float) parentRect.height() / (float) childHeight;
        if (scaleY > scaleX) {
            scale = scaleY;
            dx = parentRect.left + (parentRect.width() - childWidth * scale) * 0.5f;
            dy = parentRect.top;
        } else {
            scale = scaleX;
            dx = parentRect.left;
            dy = parentRect.top + (parentRect.height() - childHeight * scale) * 0.5f;
        }
        outTransform.setScale(scale, scale);
        outTransform.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        outTransform.postScale(1 + mScale, 1 + mScale, parentRect.width() / 2, parentRect.height() / 2);
        return outTransform;
    }
}
