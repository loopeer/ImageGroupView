package com.loopeer.android.librarys.imagegroupview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.ImageDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.Image;

public class ImageAdapter extends RecyclerViewAdapter<Image> {


    public ImageAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(final Image product, final int i, RecyclerView.ViewHolder viewHolder) {
        ImageViewHolder productViewHolder = (ImageViewHolder) viewHolder;
        productViewHolder.bind(product);
        productViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.list_item_image, parent, false);
        return new ImageViewHolder(view);
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
}
