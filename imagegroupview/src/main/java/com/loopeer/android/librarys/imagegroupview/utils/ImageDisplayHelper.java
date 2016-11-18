/**
 * Created by YuGang Yang on April 20, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.loopeer.android.librarys.imagegroupview.utils;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.loopeer.android.librarys.imagegroupview.photodraweeview.PhotoDraweeView;

import java.io.File;

public final class ImageDisplayHelper {

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

    public static void displayImage(final PhotoDraweeView draweeView, String path) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        Uri uri = createNetWorkImageUri(path);

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(uri);
        controller.setOldController(draweeView.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null || draweeView == null) {
                    return;
                }
                draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        draweeView.setController(controller.build());
    }

    public static void displayImageLocal(SimpleDraweeView draweeView, String path) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        Uri uri = Uri.fromFile(new File(path));

        draweeView.setImageURI(uri);
    }

    public static void displayImageLocal(final PhotoDraweeView draweeView, String path) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        Uri uri = Uri.fromFile(new File(path));

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(uri);
        controller.setOldController(draweeView.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null || draweeView == null) {
                    return;
                }
                draweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        draweeView.setController(controller.build());
    }

    public static void displayImageLocal(SimpleDraweeView draweeView, String path, int width, int height) {
        if (draweeView == null || TextUtils.isEmpty(path)) {
            return;
        }

        Uri uri = Uri.fromFile(new File(path));
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

    public static void setImageScaleType(SimpleDraweeView draweeView, ScalingUtils.ScaleType scaleType) {
        draweeView.getHierarchy().setActualImageScaleType(scaleType);
    }

}
