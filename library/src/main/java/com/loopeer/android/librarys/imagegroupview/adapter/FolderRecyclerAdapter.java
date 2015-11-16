package com.loopeer.android.librarys.imagegroupview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.ImageDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;

public class FolderRecyclerAdapter extends RecyclerViewAdapter<ImageFolder> {


    public FolderRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(ImageFolder var1, int var2, RecyclerView.ViewHolder var3) {
        AlbumViewHolder albumViewHolder = (AlbumViewHolder) var3;
        albumViewHolder.bind(var1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView mImage;
        TextView mTextAlbumName;
        TextView mTextSize;

        public AlbumViewHolder(View view) {
            super(view);
            mImage = (SimpleDraweeView) view.findViewById(R.id.image_album);
            mTextAlbumName = (TextView) view.findViewById(R.id.text_album_name);
            mTextSize = (TextView) view.findViewById(R.id.text_album_size);

        }

        void bind(ImageFolder imageFloder) {
            ImageDisplayHelper.displayImageLocal(mImage, imageFloder.firstImagePath, 200, 200);
            mTextAlbumName.setText(imageFloder.name);
            mTextSize.setText(Integer.toString(imageFloder.count));
        }
    }
}
