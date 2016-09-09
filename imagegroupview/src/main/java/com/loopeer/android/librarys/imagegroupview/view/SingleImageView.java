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
import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupDisplayHelper;

import java.util.List;

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
        new AlertDialog.Builder(getContext())
                .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            doTakePhoto();
                        } else {
                            doAlbum();
                        }
                    }
                })
                .show();
    }

    public void doTakePhoto() {
        NavigatorImage.startCustomAlbumActivity(getContext(), 1, getId(), AlbumActivity.TAKE_PHOTO);
    }

    public void doAlbum() {
        NavigatorImage.startCustomAlbumActivity(getContext(), 1, getId(), AlbumActivity.ALBUM);
    }

    @Override
    public void onClick(View v) {
        doUpLoadPhotoClick();
    }

    public void onParentResult(int requestCode, Intent data) {
        if (data == null) return;
        List<String> images = data.getStringArrayListExtra(NavigatorImage.EXTRA_PHOTOS_URL);
        if (requestCode == NavigatorImage.RESULT_SELECT_PHOTOS && null != images) {
            refreshLocalImage(images.get(0));
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
