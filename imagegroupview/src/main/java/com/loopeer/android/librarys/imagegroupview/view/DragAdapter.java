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
    //    private List<String> strList;//测试的字符串
    private List<SquareImage> mData;//九宫格图片
    private int hidePosition = AdapterView.INVALID_POSITION;

    public DragAdapter(Context context, List<SquareImage> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
//        return strList.size();
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
//        TextView view;
        final SquareImageView squareView;
        if (convertView == null) {
//            view = new TextView(context);
            squareView = new SquareImageView(context);
            squareView.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
//            squareView.setTag(getTag(position));
        } else {
//            view = (TextView) convertView;
            squareView = (SquareImageView) convertView;
        }
        /*//hide时隐藏Text
        if (position != hidePosition) {
            view.setText(strList.get(position));
        } else {
            view.setText("");
        }*/
//        view.setId(position);
        if (position != hidePosition) {
            bindData(squareView, position);
        } else {
            //
        }
        squareView.setId(position);
        return squareView;
    }

    public void hideView(int position) {
        hidePosition = position;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int position) {
//        strList.remove(position);
        mData.remove(position);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
//从前向后拖动，其他item依次前移
        if (draggedPos < destPos) {
            /*strList.add(destPos + 1, getItem(draggedPos));
            strList.remove(draggedPos);*/
            mData.add(destPos + 1, getItem(draggedPos));
            mData.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if (draggedPos > destPos) {
            /*strList.add(destPos, getItem(draggedPos));
            strList.remove(draggedPos + 1);*/
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
