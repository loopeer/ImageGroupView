package com.loopeer.android.librarys.imagegroupview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.ImageDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    private List<ImageFolder> mFolders = new ArrayList<>();

    int lastSelected = 0;

    public FolderAdapter(Context context){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<ImageFolder> data) {
        this.setData(data);
        this.notifyDataSetChanged();
    }

    public void setData(List<ImageFolder> data) {
        this.mFolders.clear();
        if (data != null) {
            this.mFolders.addAll(data);
        }

    }

    @Override
    public int getCount() {
        return mFolders.size();
    }

    @Override
    public ImageFolder getItem(int i) {
        return mFolders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.list_item_album, viewGroup, false);
            holder = new ViewHolder(view);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            holder.bindData(getItem(i));
            if(lastSelected == i){
                //holder.indicator.setVisibility(View.VISIBLE);
            }else{
                //holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize(){
        int result = 0;
        if(mFolders != null && mFolders.size()>0){
            for (ImageFolder f: mFolders){
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if(lastSelected == i) return;

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex(){
        return lastSelected;
    }

    class ViewHolder{

        SimpleDraweeView mImage;
        TextView mTextAlbumName;
        TextView mTextSize;

        ViewHolder(View view){
            mImage = (SimpleDraweeView) view.findViewById(R.id.image_album);
            mTextAlbumName = (TextView) view.findViewById(R.id.text_album_name);
            mTextSize = (TextView) view.findViewById(R.id.text_album_size);
            //indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        void bindData(ImageFolder imageFloder) {
            ImageDisplayHelper.displayImageLocal(mImage, imageFloder.firstImagePath, 200, 200);
            mTextAlbumName.setText(imageFloder.name);
            mTextSize.setText(Integer.toString(imageFloder.count));
        }
    }
}
