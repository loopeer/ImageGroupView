package com.loopeer.android.librarys.imagegroupview.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupDisplayHelper;

import java.lang.reflect.Method;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SingleImageView extends SimpleDraweeView implements View.OnClickListener {

    private SquareImage mImage;
    private int mPlaceholderDrawable;

    public SingleImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init();
    }

    public SingleImageView(Context context) {
        super(context);
        init();
    }

    public SingleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        setOnClickListener(this);
        mImage = new SquareImage();
    }

    public void doUpLoadPhotoClick() {
        doUpLoadPhotoClick(1, 1);
    }

    public void doUpLoadPhotoClick(final int aspectRatioX, final int aspectRatioY) {
        new AlertDialog.Builder(getContext())
                .setItems(new String[]{getContext().getString(R.string.take_photo),
                                getContext().getString(R.string.select_images)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    doTakePhoto(aspectRatioX, aspectRatioY);
                                } else {
                                    doAlbum(aspectRatioX, aspectRatioY);
                                }
                            }
                        })
                .show();
    }

    public void doTakePhoto(int aspectRatioX, int aspectRatioY) {
        NavigatorImage.startAvatarAlbumActivity(getContext(), getId(), AlbumActivity.TAKE_PHOTO, aspectRatioX, aspectRatioY);
    }

    public void doAlbum(int aspectRatioX, int aspectRatioY) {
        NavigatorImage.startAvatarAlbumActivity(this, getContext(), getId(), AlbumActivity.ALBUM, aspectRatioX, aspectRatioY);
    }

    @Override
    public void onClick(View v) {
        doUpLoadPhotoClick();
    }

    public void startActivityForResultReflect(Intent intent, int requestCode) {
        Method method;
        try {
            method = this.getClass().getMethod("startActivityForResult", Intent.class, int.class);
            method.invoke(this, intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            onParentResult(requestCode, data);
    }

    public void onParentResult(int requestCode, Intent data) {
        if (data == null) return;
        List<Image> images = (List<Image>) data.getSerializableExtra(NavigatorImage.EXTRA_PHOTOS_URL);
        if (requestCode == NavigatorImage.RESULT_SELECT_PHOTOS && null != images) {
            refreshLocalImage(images.get(0).url);
        }
    }

    private void refreshLocalImage(String url) {
        mImage.localUrl = url;
        updateView();
    }

    private void updateView() {
        if (mImage.localUrl != null) {
            ImageGroupDisplayHelper.displayImageLocal(this, mImage.localUrl, 100, 100);
        } else if (mImage.interNetUrl != null) {
            ImageGroupDisplayHelper.displayImage(this, mImage.interNetUrl, mPlaceholderDrawable, 100, 100);
        } else {
            setImageResource(mPlaceholderDrawable);
        }
    }

    public void updateImage(SquareImage squareImage) {
        mImage = squareImage;
        updateView();
    }

    public String getLocalUrl() {
        if (mImage == null) return null;
        return mImage.localUrl;
    }

    @NonNull
    private String getPhotoKey() {
        return "image_" + System.currentTimeMillis();
    }
}
