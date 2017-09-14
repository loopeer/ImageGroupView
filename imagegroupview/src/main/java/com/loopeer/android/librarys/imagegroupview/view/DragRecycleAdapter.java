package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

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
        view.setOnClickListener(this);//将创建的View注册点击事件
        view.setOnLongClickListener(this);
        return new DragViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DragViewHolder holder, int position) {
//        holder.squareView.setImageData(mData.get(position));
        holder.tv.setText(mData.get(position).localUrl);
        holder.itemView.setTag(position);//将position保存在itemView的Tag中，以便点击时进行获取
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
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onRecyclerViewItemLongClickListener != null){
            onRecyclerViewItemLongClickListener.onItemLongClick(v, (int) v.getTag());
        }
        return true;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.onRecyclerViewItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.onRecyclerViewItemLongClickListener = listener;
    }

    static class DragViewHolder extends RecyclerView.ViewHolder {
        SquareImageView squareView;
        TextView tv;
        View mView;

        DragViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
//            squareView = (SquareImageView) itemView.findViewById(R.id.sq);
            tv = (TextView) itemView.findViewById(R.id.tv);
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

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int pos);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int pos);
    }

}
