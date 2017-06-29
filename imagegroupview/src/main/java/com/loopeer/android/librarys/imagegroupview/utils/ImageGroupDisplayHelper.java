/**
 * Created by YuGang Yang on April 20, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.loopeer.android.librarys.imagegroupview.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.io.File;

public final class ImageGroupDisplayHelper {

    public static Uri createNetWorkImageUri(String path) {
        return Uri.parse(path);
    }

    public static void displayImage(final SimpleDraweeView draweeView, String path, final int failPlaceHolder) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                draweeView.setImageResource(failPlaceHolder);
            }
        };

        Uri uri = createNetWorkImageUri(path);
        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener).setUri(uri).setImageRequest(request).build();

        draweeView.setController(controller);

    }

    public static void displayImage(final SimpleDraweeView draweeView, String path, final int failPlaceHolder, int width, int height) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                draweeView.setImageResource(failPlaceHolder);
            }
        };

        Uri uri = createNetWorkImageUri(path);
        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    public static void displayImage(final SimpleDraweeView draweeView, String path, int width, int height) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        Uri uri = createNetWorkImageUri(path);
        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    public static void displayImage(final SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (draweeView == null || uri == null) {
            return;
        }

        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    public static void displayImage(SimpleDraweeView draweeView, String path) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }


        Uri uri = createNetWorkImageUri(path);
        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    public static void displayImageOneShow(final SimpleDraweeView draweeView, String path, final int maxSize) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        Postprocessor reSizeProcessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "redMeshPostprocessor";
            }

            @Override
            public void process(Bitmap bitmap) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                ViewGroup.LayoutParams layoutParams = draweeView.getLayoutParams();
                int tmp = maxSize;
                if (width < height) {
                    width = width * tmp / height;
                    height = tmp;
                } else {
                    height = height * tmp / width;
                    width = tmp;
                }
                layoutParams.width = width;
                layoutParams.height = height;

                draweeView.setLayoutParams(layoutParams);
            }
        };

        Uri uri = createNetWorkImageUri(path);
        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new ResizeOptions(500, 500))
                .setAutoRotateEnabled(true)
                .setPostprocessor(reSizeProcessor)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    public static void displayImageLocal(SimpleDraweeView draweeView, String path, int width, int height) {
        if (draweeView == null) {
            return;
        }
        Uri uri = TextUtils.isEmpty(path) ? null : Uri.fromFile(new File(path));
        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    public static void displayImageLocal(SimpleDraweeView draweeView, Uri uri, int width, int height) {
        if (draweeView == null || uri == null) {
            return;
        }

        ImageRequest
                request = ImageRequestBuilder.newBuilderWithSource(uri).setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

}
