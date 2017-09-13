package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

import java.util.ArrayList;
import java.util.List;

public class DragGridAdapters extends BaseAdapter {

    private final static String TAG = "DragGridAdapter";
    private Context mContext;
    private int holdPosition;//控制position
    private boolean isChange = false;//是否改变
    private boolean isVisible = true;//是否可见
    public List<DragGridView> channelList;//可以拖动的列表
    private List<SquareImage> mData;
    private DragGridAdapters.OnSquareClickListener mClickListener;
    private DragGridAdapters.OnSquareLongClickListener mLongClickListener;
    private int mAddButtonDrawable;
    private int mPlaceholderDrawable;
    private boolean mRoundAsCircle;
    private boolean isDelete;
    public int remove_position = -1;//要删除的position
    public int hide_position;//指定隐藏的position

    public DragGridAdapters(Context c, DragGridAdapters.OnSquareClickListener listener) {
        mContext = c;
        mData = new ArrayList<>();
        mClickListener = listener;
    }

    public DragGridAdapters(Context c, DragGridAdapters.OnSquareClickListener listener, DragGridAdapters.OnSquareLongClickListener longListener) {
        mContext = c;
        mData = new ArrayList<>();
        mClickListener = listener;
        mLongClickListener = longListener;
    }

    public void updateParam(int addButtonDrawable, int placeholderDrawable, boolean roundAsCircle) {
        mAddButtonDrawable = addButtonDrawable;
        mPlaceholderDrawable = placeholderDrawable;
        mRoundAsCircle = roundAsCircle;
        notifyDataSetChanged();
    }

    public void updateData(List<SquareImage> datas, boolean showAddButton) {
        setData(datas, showAddButton);
        notifyDataSetChanged();
    }

    public void setData(List<SquareImage> datas, boolean showAddButton) {
        mData.clear();
        mData.addAll(datas);
        if (showAddButton) {
            mData.add(null);
        }
    }

    public int getCount() {
        return mData.size();
    }

    public SquareImage getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView squareView;
        if (convertView == null || !convertView.getTag().equals(getTag(position))) {
            squareView = new SquareImageView(mContext);
            squareView.setPlaceholderDrawable(mPlaceholderDrawable);
            squareView.setRoundAsCircle(mRoundAsCircle);
            squareView.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
            squareView.setTag(getTag(position));
        } else {
            squareView = (SquareImageView) convertView;
        }

        bindData(squareView, position);
        return squareView;
    }

    @NonNull
    private String getTag(int position) {
        return "POSITION" + position;
    }

    private void bindData(SquareImageView squareView, final int position) {
        if (getItem(position) == null) {
            squareView.setImageResource(mAddButtonDrawable);
        } else {
            squareView.setImageData(getItem(position));
        }
        squareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.photoClick(v, getItem(position), position);
            }
        });
        squareView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mLongClickListener.photoLongClick(v, getItem(position), position);
                return true;
            }
        });
    }

    interface OnSquareClickListener {
        void photoClick(View v, SquareImage squareImage, int position);
    }

    interface OnSquareLongClickListener {//长点击事件，用于长按滑动图片

        void photoLongClick(View v, SquareImage squareImage, int position);
    }
}
