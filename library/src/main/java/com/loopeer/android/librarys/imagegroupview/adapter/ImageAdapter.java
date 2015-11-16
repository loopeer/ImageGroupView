package com.loopeer.android.librarys.imagegroupview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.ImageDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerViewAdapter<Image> {

    private static final int ITEM_CAMERA = 10000;
    private static final int ITEM_IMAMGE = 10001;

    private OnImageClickListener mOnImageClickListener;
    private List<Image> mSelectImages;

    public ImageAdapter(Context context) {
        super(context);
        mSelectImages = new ArrayList<>();
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    @Override
    public void bindView(final Image product, final int i, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ImageViewHolder) {
            ImageViewHolder productViewHolder = (ImageViewHolder) viewHolder;
            productViewHolder.bind(product);
            productViewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnImageClickListener.onImageSelected(product);
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

    private boolean isImageSelected(Image product) {
        return mSelectImages.contains(product);
    }

    public void updateFolderImageData(ImageFolder imageFolder) {
        List<Image> images = new ArrayList();
        images.addAll(imageFolder.images);
        if (TextUtils.isEmpty(imageFolder.dir)) {
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
                return new ImageViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) == null) {
            return ITEM_CAMERA;
        }
        return ITEM_IMAMGE;
    }

    public void updateSelectImages(List<Image> selectedImages) {
        mSelectImages.clear();
        mSelectImages.addAll(selectedImages);
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView icon;
        FrameLayout container;

        public ImageViewHolder(View itemView) {
            super(itemView);

            icon = (SimpleDraweeView) itemView.findViewById(android.R.id.icon);
            container = (FrameLayout) itemView.findViewById(R.id.container);

            final int screenWidth = itemView.getResources().getDisplayMetrics().widthPixels;
            final int parentMargin = itemView.getResources().getDimensionPixelSize(R.dimen.inline_padding);
            final int width = (screenWidth - parentMargin * 4) / 3;
            ViewGroup.LayoutParams layoutParams = icon.getLayoutParams();
            layoutParams.height = width;
            layoutParams.width = width;
            icon.setLayoutParams(layoutParams);

        }

        public void bind(Image image) {
            ImageDisplayHelper.displayImageLocal(icon, image.url, 200, 200);
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

    public interface OnImageClickListener{
        void onImageSelected(Image image);
        void onCameraSelected();
    }
}
