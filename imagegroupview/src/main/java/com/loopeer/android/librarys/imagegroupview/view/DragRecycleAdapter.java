package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

import java.util.List;

public class DragRecycleAdapter extends RecyclerView.Adapter<DragRecycleAdapter.DragViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private Context context;
    private List<SquareImage> mData;//九宫格图片
    private int hidePosition = AdapterView.INVALID_POSITION;
    OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = null;
    OnRecyclerViewItemLongClickListener onRecyclerViewItemLongClickListener = null;

    public DragRecycleAdapter(Context context, List<SquareImage> data) {
        this.context = context;
        this.mData = data;
    }

    @Override
    public DragViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        view.setOnClickListener(this);

        return new DragViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DragViewHolder holder, int position) {
        holder.squareView.setImageData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.onRecyclerViewItemClickListener = listener;
    }

    static class DragViewHolder extends RecyclerView.ViewHolder {
        SquareImageView squareView;
        View mView;

        DragViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            squareView = (SquareImageView) itemView.findViewById(R.id.sq);
        }

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
            mData.add(destPos + 1, mData.get(draggedPos));
            mData.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if (draggedPos > destPos) {
            mData.add(destPos, mData.get(draggedPos));
            mData.remove(draggedPos + 1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
    }

    interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int pos);
    }

    interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int pos);
    }

}
