package com.loopeer.android.librarys.imagegroupview.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;
import com.loopeer.android.librarys.imagegroupview.utils.AnimatorScaleType;
import com.loopeer.android.librarys.imagegroupview.utils.ImageDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.view.ScaleImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerViewAdapter<Image> {

    private static final int ITEM_CAMERA = 10000;
    private static final int ITEM_IMAGE = 10001;
    private static final int ANIMATOR_TIME = 600;
    private static final float ZOOM_IMAGE_SCALE = AnimatorScaleType.ZOOM_SCALE;
    private static final float REDUCE_IMAGE_SCALE = AnimatorScaleType.REDUCE_SCALE;

    private OnImageClickListener mOnImageClickListener;
    private List<Image> mSelectImages;
    private int mAlbumType;
    private boolean mIsAvatarType;

    public ImageAdapter(Context context) {
        super(context);
        mSelectImages = new ArrayList<>();
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    public void setAlbumType(int albumType) {
        mAlbumType = albumType;
    }

    public void setIsAvatarType(boolean isAvatarType) {
        mIsAvatarType = isAvatarType;
    }

    @Override
    public void bindView(final Image product, final int i, RecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        if (viewHolder instanceof ImageViewHolder) {
            final ImageViewHolder productViewHolder = (ImageViewHolder) viewHolder;
            productViewHolder.bind(product, !payloads.isEmpty(), isImageSelected(product));
            productViewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //0:无反应  1:选中  2:取消选中
                    int index = mOnImageClickListener.onImageSelected(product, i);
                    if (index == 1) {
                        showSelectedNumberAnimator((ViewGroup) productViewHolder.itemView);
                    }
                    if (index != 0) {
                        if (isImageSelected(product)) {
                            zoomImageScaleAnimator(productViewHolder.getImage());
                        } else {
                            reduceImageScaleAnimator(productViewHolder.getImage());
                        }
                    }
                }
            });
            productViewHolder.itemView.setSelected(isImageSelected(product));
        }
        if (viewHolder instanceof CameraViewHolder) {
            CameraViewHolder productViewHolder = (CameraViewHolder) viewHolder;
            productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnImageClickListener.onCameraSelected();
                }
            });
        }
    }

    private void zoomImageScaleAnimator(ScaleImageView draweeView) {
        getScaleAnimator(draweeView, REDUCE_IMAGE_SCALE, ZOOM_IMAGE_SCALE).start();
    }

    private void reduceImageScaleAnimator(ScaleImageView draweeView) {
        getScaleAnimator(draweeView, ZOOM_IMAGE_SCALE, REDUCE_IMAGE_SCALE).start();
    }

    private ValueAnimator getScaleAnimator(final ScaleImageView view, float from, float to) {
        ValueAnimator animator = ValueAnimator.ofFloat(from, to);
        animator.setDuration(ANIMATOR_TIME);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                AnimatorScaleType.INSTANCE.setScale((float) animation.getAnimatedValue());
//                ImageDisplayHelper.setImageScaleType(view, AnimatorScaleType.INSTANCE);

                view.setScale((float) animation.getAnimatedValue());
            }
        });
        return animator;
    }

    private void showSelectedNumberAnimator(final ViewGroup view) {
        final TextView tv = new TextView(view.getContext());
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(getNumberAnimatorSize(view));
        tv.setText(String.valueOf(selectedImageNumber()));
        tv.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.animator_text_bg));
        tv.setGravity(Gravity.CENTER);
        tv.setWidth(view.getWidth());
        tv.setHeight(view.getHeight());

        view.addView(tv, 1);

        ObjectAnimator animator = ObjectAnimator.ofFloat(tv, View.ALPHA, 1.0f, 0.0f);
        animator.setDuration(ANIMATOR_TIME);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.removeView(tv);
            }
        });
        animator.start();
    }

    private int getNumberAnimatorSize(View view) {
        return view.getWidth() / 9;
    }

    private int selectedImageNumber() {
        return mSelectImages.size();
    }

    private boolean isImageSelected(Image product) {
        return mSelectImages.contains(product);
    }

    public void updateFolderImageData(ImageFolder imageFolder) {
        List<Image> images = new ArrayList();
        images.addAll(imageFolder.images);
        if (TextUtils.isEmpty(imageFolder.dir) && mAlbumType != AlbumActivity.ALBUM) {
            images.add(0, null);
        }
        updateData(images);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final LayoutInflater inflater = getLayoutInflater();
        final View view;
        switch (i) {
            case ITEM_CAMERA:
                view = inflater.inflate(R.layout.list_item_camera, parent, false);
                return new CameraViewHolder(view);
            default:
                view = inflater.inflate(R.layout.list_item_image, parent, false);
                return new ImageViewHolder(view, mIsAvatarType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null) {
            return ITEM_CAMERA;
        }
        return ITEM_IMAGE;
    }

    public void updateSelectImages(List<Image> selectedImages, int position) {
        mSelectImages.clear();
        mSelectImages.addAll(selectedImages);
        notifyItemChanged(position, "make it not empty");
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ScaleImageView icon;
        FrameLayout container;
        ImageView mImgCheck;
        private int mWidth;

        public ImageViewHolder(View itemView, boolean isAvatarType) {
            super(itemView);

            icon = (ScaleImageView) itemView.findViewById(android.R.id.icon);
            container = (FrameLayout) itemView.findViewById(R.id.container);
            mImgCheck = (ImageView) itemView.findViewById(R.id.img_check);

            final int screenWidth = itemView.getResources().getDisplayMetrics().widthPixels;
            final int parentMargin = itemView.getResources().getDimensionPixelSize(R.dimen.inline_padding);
            mWidth = (screenWidth - parentMargin * 4) / 3;
            ViewGroup.LayoutParams layoutParams = icon.getLayoutParams();
            layoutParams.height = mWidth;
            layoutParams.width = mWidth;
            icon.setLayoutParams(layoutParams);
            if (isAvatarType) {
                mImgCheck.setVisibility(View.INVISIBLE);
            }
        }

        public ScaleImageView getImage() {
            return icon;
        }

        public void bind(Image image, boolean isDoAnimator, boolean isSelected) {
            ImageDisplayHelper.displayImageLocal(icon, image.url, mWidth, mWidth);
            if (!isDoAnimator) {
//                ImageDisplayHelper.setImageScaleType(icon, isSelected ?
//                        AnimatorScaleType.getZoomScaleType() : AnimatorScaleType.getReduceScaleType());
                icon.setScale(isSelected ? ScaleImageView.getZoomScale() : ScaleImageView.getReduceScale());
            }
        }
    }

    static class CameraViewHolder extends RecyclerView.ViewHolder {

        public CameraViewHolder(View itemView) {
            super(itemView);
            final int screenWidth = itemView.getResources().getDisplayMetrics().widthPixels;
            final int parentMargin = itemView.getResources().getDimensionPixelSize(R.dimen.inline_padding);
            final int width = (screenWidth - parentMargin * 4) / 3;
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = width;
            layoutParams.width = width;
            itemView.setLayoutParams(layoutParams);

        }
    }

    public interface OnImageClickListener {
        int onImageSelected(Image image, int position);//0:无反应  1:选中  2:取消选中

        void onCameraSelected();
    }
}
