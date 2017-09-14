package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

import java.util.List;

public class DragAdapter extends BaseAdapter {
    private Context context;
    private List<SquareImage> mData;//九宫格图片
    private int hidePosition = AdapterView.INVALID_POSITION;

    public DragAdapter(Context context, List<SquareImage> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public SquareImage getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SquareImageView squareView;
        if (convertView == null) {
            squareView = new SquareImageView(context);
            squareView.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
        } else {
            squareView = (SquareImageView) convertView;
        }
        //hide时隐藏
        if (position != hidePosition) {
            squareView.setVisibility(View.VISIBLE);
            bindData(squareView, position);
        } else {
            squareView.setVisibility(View.INVISIBLE);
        }
        squareView.setId(position);
        squareView.setClickAble(true);
        return squareView;
    }

    void hideView(int position) {
        hidePosition = position;
        notifyDataSetChanged();
    }

    void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int position) {
        mData.remove(position);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if (draggedPos < destPos) {
            mData.add(destPos + 1, getItem(draggedPos));
            mData.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if (draggedPos > destPos) {
            mData.add(destPos, getItem(draggedPos));
            mData.remove(draggedPos + 1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
    }

    private void bindData(SquareImageView squareView, final int position) {
        squareView.setImageData(getItem(position));
    }

}
