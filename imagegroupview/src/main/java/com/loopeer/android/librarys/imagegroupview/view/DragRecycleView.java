package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

public class DragRecycleView extends RecyclerView {
    private static final int DRAG_IMG_SHOW = 1;//滑动状态
    private static final int DRAG_IMG_NOT_SHOW = 0;//非滑动状态
    private static final float AMP_FACTOR = 1.2f;
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
        //初始化用于设置dragImageView的参数对象
        dragImageView = new ImageView(getContext());
        dragImageView.setTag(DRAG_IMG_NOT_SHOW);
        dragImageViewParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }
}
