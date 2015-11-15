package com.loopeer.android.librarys.imagegroupview;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.loopeer.android.librarys.imagegroupview.photodraweeview.OnViewTapListener;
import com.loopeer.android.librarys.imagegroupview.photodraweeview.PhotoDraweeView;
import java.io.File;
import java.io.FileNotFoundException;

public class ScaleImageFragment extends Fragment {

    private SimpleDraweeView viewPlaceholder;
    private PhotoDraweeView viewScale;
    private OnTabOneClickListener listener;
    private SquareImage squareImage;

    public static ScaleImageFragment newInstance(SquareImage image) {
        ScaleImageFragment scaleImageFragment = new ScaleImageFragment();
        scaleImageFragment.squareImage = image;
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

        updateView(view);
        setUpdata();
    }

    private void updateView(View view) {
        viewPlaceholder = (SimpleDraweeView) view.findViewById(R.id.image_scale_placeholder);
        viewScale = (PhotoDraweeView) view.findViewById(R.id.image_scale_image);
        viewScale.getAttacher().setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (viewScale.getScale() == viewScale.getMinimumScale() && listener != null) {
                    listener.onTabOneClick();
                } else {
                    viewScale.getAttacher().setScale(viewScale.getMinimumScale(), x, y, true);
                }
            }
        });
        viewScale.getAttacher().setOnLongClickListener(new View.OnLongClickListener() {
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

    private void doSaveImage(String imageUrl) {
        ImageRequest downloadRequest = ImageRequest.fromUri(Uri.parse(imageUrl));
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(downloadRequest);
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

    private void setUpdata() {
        switch (squareImage.type) {
            case LOCAL:
                ImageDisplayHelper.displayImageLocal(viewPlaceholder, squareImage.localUrl, 200, 200);
                ImageDisplayHelper.displayImageLocal(viewScale, squareImage.localUrl);
                break;
            case INTER:
                ImageDisplayHelper.displayImage(viewPlaceholder, squareImage.interNetUrl, 200, 200);
                ImageDisplayHelper.displayImage(viewScale, squareImage.interNetUrl);
                break;
        }
    }

}
