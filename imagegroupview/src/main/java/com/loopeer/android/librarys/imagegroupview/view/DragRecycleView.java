package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

public class DragRecycleView extends RecyclerView {
    private static final int DRAG_IMG_SHOW = 1;//滑动状态
    private static final int DRAG_IMG_NOT_SHOW = 0;//非滑动状态
    private static final String TAG = "DragRecycleView";

    private ImageView dragImageView;//滑动的imageView
    private WindowManager.LayoutParams dragImageViewParams;
    private WindowManager windowManager;
    private boolean isViewOnDrag = false;

    private int preDraggedOverPosition = AdapterView.INVALID_POSITION;//滑动前的位置
    private int downRawX;
    private int downRawY;


    public DragRecycleView(Context context) {
        super(context);
        init();
    }

    public DragRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //初始化显示被拖动item的imageView
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("DragRecycleViewLog ", " longClick ");
                return true;
            }
        });
        dragImageView = new ImageView(getContext());
        dragImageView.setTag(DRAG_IMG_NOT_SHOW);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //获取触摸点相对于屏幕的坐标
            downRawX = (int) ev.getRawX();
            downRawY = (int) ev.getRawY();
        }
        //dragImageView处于被拖动时，更新dragImageView位置
        else if ((ev.getAction() == MotionEvent.ACTION_MOVE) && (isViewOnDrag)) {
            Log.i(TAG, "" + ev.getRawX() + " " + ev.getRawY());
            //设置触摸点为dragImageView中心
            dragImageViewParams.x = (int) (ev.getRawX() - dragImageView.getWidth() / 2);
            dragImageViewParams.y = (int) (ev.getRawY() - dragImageView.getHeight() / 2);
            //更新窗口显示
            windowManager.updateViewLayout(dragImageView, dragImageViewParams);
            //获取当前触摸点的item position
//            int mFirstPosition = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
//            int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
            //如果当前停留位置item不等于上次停留位置的item，交换本次和上次停留的item
/*
            if ((currDraggedPosition != AdapterView.INVALID_POSITION) && (currDraggedPosition != preDraggedOverPosition)) {
                ((DragRecycleAdapter) getAdapter()).swapView(preDraggedOverPosition, currDraggedPosition);
                preDraggedOverPosition = currDraggedPosition;
            }
*/
        }
        //释放dragImageView
        else if ((ev.getAction() == MotionEvent.ACTION_UP) && (isViewOnDrag)) {
            ((DragRecycleAdapter) getAdapter()).showHideView();
            if ((int) dragImageView.getTag() == DRAG_IMG_SHOW) {
                windowManager.removeView(dragImageView);
                dragImageView.setTag(DRAG_IMG_NOT_SHOW);
            }
            isViewOnDrag = false;
        }
        return super.onTouchEvent(ev);
    }
}
