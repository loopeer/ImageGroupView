package com.loopeer.android.librarys.imagegroupview.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.loopeer.android.librarys.imagegroupview.OnTabOneClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.photodraweeview.OnViewTapListener;
import com.loopeer.android.librarys.imagegroupview.photodraweeview.PhotoDraweeView;
import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;
import com.loopeer.android.librarys.imagegroupview.utils.ImageDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.view.FixedNestedScrollView;

import java.io.File;
import java.io.FileNotFoundException;

public class ScaleImageFragment extends Fragment {

    private SimpleDraweeView mViewPlaceholder;
    private PhotoDraweeView mViewScale;
    private OnTabOneClickListener listener;
    private SquareImage squareImage;
    private int placeholderDrawable;
    private FixedNestedScrollView mScrollView;
    private float mImageHeight;
    private float mImageWidth;
    private boolean mHasLoadSuccess;
    private ProgressBar mProgressBar;

    public static ScaleImageFragment newInstance(SquareImage image, int placeholderDrawable) {
        ScaleImageFragment scaleImageFragment = new ScaleImageFragment();
        scaleImageFragment.squareImage = image;
        scaleImageFragment.placeholderDrawable = placeholderDrawable;
        return scaleImageFragment;
    }

    public void setOneTabListener(OnTabOneClickListener listener) {
        this.listener = listener;
    }

    public void setSquareImage(SquareImage squareImage) {
        this.squareImage = squareImage;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scale_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPlaceholder = (SimpleDraweeView) view.findViewById(R.id.image_scale_placeholder);
        mScrollView = (FixedNestedScrollView) view.findViewById(R.id.container_scroll);
        mViewScale = (PhotoDraweeView) view.findViewById(R.id.image_scale_image);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_scale_image);

        initImageSize();
        setUpPlaceHolderView();
        setViewScaleListener();
        setupData();
    }

    private void initImageSize() {
        mImageWidth = DisplayUtils.getScreenWidth(getContext());
        mImageHeight = DisplayUtils.getScreenHeight(getContext()) - DisplayUtils.getStatusBarHeight(getContext());
    }

    public boolean getImageSuccess() {
        return mHasLoadSuccess;
    }

    public float getImageHeight() {
        return mImageHeight;
    }

    public float getImageWidth() {
        return mImageWidth;
    }

    public View getContainerScrollView() {
        return mScrollView;
    }

    public View getViewScaleView() {
        return mViewScale;
    }

    public View getPlaceholderView() {
        return mViewPlaceholder;
    }

    private void setViewScaleListener() {
        mViewScale.getAttacher().setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (mViewScale.getScale() == mViewScale.getMinimumScale() && listener != null) {
                    listener.onTabOneClick();
                } else {
                    mViewScale.getAttacher().setScale(mViewScale.getMinimumScale(), x, y, true);
                }
            }
        });
        mViewScale.getAttacher().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("保存该图片？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doSaveImage(squareImage.interNetUrl);
                    }
                }).setNegativeButton("取消", null);
                builder.show();
                return false;
            }
        });
    }

    public void setUpPlaceHolderView() {
        mProgressBar.setVisibility(View.VISIBLE);
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(ContextCompat.getDrawable(getContext(), placeholderDrawable))
                .setPlaceholderImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        mViewPlaceholder.setHierarchy(hierarchy);
    }

    private void setupData() {
        switch (squareImage.type) {
            case LOCAL:
                ImageDisplayHelper.displayImageLocal(mViewPlaceholder, squareImage.localUrl, 200, 200);
                ImageDisplayHelper.displayImageLocal(mViewScale, squareImage.localUrl, mControllerListener);
                break;
            case NETWORK:
                ImageDisplayHelper.displayImage(mViewPlaceholder, squareImage.interNetUrl, 200, 200);
                ImageDisplayHelper.displayImage(mViewScale, squareImage.interNetUrl, mControllerListener);
                break;
        }
    }

    private ControllerListener mControllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            if (imageInfo == null) {
                return;
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            mHasLoadSuccess = true;
            mImageHeight = (float) imageInfo.getHeight() / imageInfo.getHeight() * mImageWidth;
            mViewScale.update(imageInfo.getWidth(), imageInfo.getHeight());
        }
    };

    private void doSaveImage(String imageUrl) {
        ImageRequest downloadRequest = ImageRequest.fromUri(Uri.parse(imageUrl));
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(downloadRequest, null);
        if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
            BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
            File cacheFile = ((FileBinaryResource) resource).getFile();
            try {
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), cacheFile.getAbsolutePath(), cacheFile.getName(), "");
                Toast.makeText(getActivity(), "图片保存成功", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                showErrorMessage();
            }
        } else {
            showErrorMessage();
        }
    }

    private void showErrorMessage() {
        Toast.makeText(getActivity(), "图片正在加载中，请稍后保存", Toast.LENGTH_SHORT).show();
    }
}
