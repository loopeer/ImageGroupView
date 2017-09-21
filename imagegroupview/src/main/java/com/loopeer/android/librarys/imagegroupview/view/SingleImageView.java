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
        NavigatorImage.INSTANCE.startAvatarAlbumActivity(getContext(), getId(), AlbumActivity.Companion.getTAKE_PHOTO(), aspectRatioX, aspectRatioY);
    }

    public void doAlbum(int aspectRatioX, int aspectRatioY) {
        NavigatorImage.INSTANCE.startAvatarAlbumActivity(getContext(), getId(), AlbumActivity.Companion.getALBUM(), aspectRatioX, aspectRatioY);
    }

    @Override
    public void onClick(View v) {
        doUpLoadPhotoClick();
    }

    public void onParentResult(int requestCode, Intent data) {
        if (data == null || data.getIntExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, 0) != getId()) return;
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
        if (getHeight() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    updateView();
                }
            });
            return;
        }
        if (mImage.localUrl != null) {
            ImageGroupDisplayHelper.displayImageLocal(this, mImage.localUrl, getWidth(), getHeight());
        } else if (mImage.interNetUrl != null) {
            ImageGroupDisplayHelper.displayImage(this, mImage.interNetUrl, mPlaceholderDrawable, getWidth(), getHeight());
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
