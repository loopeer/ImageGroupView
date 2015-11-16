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

public class AlbumRecyclerAdapter extends RecyclerViewAdapter<ImageFolder> {

    public AlbumRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(ImageFolder var1, int var2, RecyclerView.ViewHolder var3) {
        if (var3 instanceof AlbumViewHolder) {
            AlbumViewHolder albumViewHolder = (AlbumViewHolder) var3;
            albumViewHolder.bind(var1);
        }
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

        public AlbumViewHolder(View itemView) {
            super(itemView);
            mImage = (SimpleDraweeView) itemView.findViewById(R.id.image_album);
            mTextAlbumName = (TextView) itemView.findViewById(R.id.text_album_name);
            mTextSize = (TextView) itemView.findViewById(R.id.text_album_size);
        }

        public void bind(ImageFolder imageFloder) {
            ImageDisplayHelper.displayImageLocal(mImage, imageFloder.firstImagePath, 200, 200);
            mTextAlbumName.setText(imageFloder.name);
            mTextSize.setText(Integer.toString(imageFloder.count));
        }
    }
}
