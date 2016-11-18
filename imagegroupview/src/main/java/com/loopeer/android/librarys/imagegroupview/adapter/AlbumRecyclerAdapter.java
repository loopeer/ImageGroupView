package com.loopeer.android.librarys.imagegroupview.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.utils.ImageDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;

import java.util.List;

public class AlbumRecyclerAdapter extends RecyclerViewAdapter<ImageFolder> {

    private ImageFolder mSelectFolder;

    public interface OnItemClickListener{
        void onItemClick(ImageFolder imageFolder);
    }

    private OnItemClickListener mOnItemClickListener;

    public AlbumRecyclerAdapter(Context context) {
        super(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public void updateData(List<ImageFolder> data) {
        mSelectFolder = data.get(0);
        super.updateData(data);
    }

    @Override
    public void bindView(final ImageFolder var1, int var2, RecyclerView.ViewHolder var3, List<Object> payloads) {
        if (var3 instanceof AlbumViewHolder) {
            AlbumViewHolder albumViewHolder = (AlbumViewHolder) var3;
            albumViewHolder.bind(var1);
            albumViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFolderSelected(var1);
                }
            });
            albumViewHolder.setFolderSelected(var1.equals(mSelectFolder));
        }
    }

    private void onFolderSelected(ImageFolder var1) {
        mSelectFolder = var1;
        mOnItemClickListener.onItemClick(var1);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView mImage;
        private TextView mTextAlbumName;
        private TextView mTextSize;
        private ImageView mSelectedIndicator;

        public AlbumViewHolder(View itemView) {
            super(itemView);

            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image_album);
            mTextAlbumName = (TextView) itemView.findViewById(R.id.text_album_name);
            mTextSize = (TextView) itemView.findViewById(R.id.text_album_size);
            mSelectedIndicator = (ImageView) itemView.findViewById(R.id.image_album_selected_indicator);
            mSelectedIndicator.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.image_group_theme_primary));
        }

        public void bind(ImageFolder imageFolder) {
            ImageDisplayHelper.displayImageLocal(mImage, imageFolder.firstImagePath, 200, 200);
            mTextAlbumName.setText(imageFolder.name);
            mTextSize.setText(Integer.toString(imageFolder.count));
        }

        public void setFolderSelected(boolean equals) {
            mSelectedIndicator.setVisibility(equals ? View.VISIBLE : View.GONE);
        }
    }
}
