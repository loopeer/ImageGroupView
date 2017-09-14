package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

public class DragView extends GridView {
    private static final int DRAG_IMG_SHOW = 1;//滑动状态
    private static final int DRAG_IMG_NOT_SHOW = 0;//非滑动状态
    private static final String TAG = "DragView";
    private static final float AMP_FACTOR = 1.2f;
    int clicks = 0;

    private ImageView dragImageView;//滑动的imageView
    private WindowManager.LayoutParams dragImageViewParams;
    private WindowManager windowManager;
    private boolean isViewOnDrag = false;


    private int preDraggedOverPosition = AdapterView.INVALID_POSITION;//滑动前的位置
    private int downRawX;
    private int downRawY;


    public DragView(Context context) {
        super(context);
        init();
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnItemLongClickListener(onLongClickListener);
        setOnItemClickListener(onItemClickListener);
        //初始化显示被拖动item的imageView
        dragImageView = new ImageView(getContext());
        dragImageView.setTag(DRAG_IMG_NOT_SHOW);
        //初始化用于设置dragImageView的参数对象
        dragImageViewParams = new WindowManager.LayoutParams();
        //获取窗口管理对象，用于后面向窗口中添加dragImageView
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("", "");
        }
    };

    private OnItemLongClickListener onLongClickListener = new OnItemLongClickListener() {
        @Override
        //长按item开始拖动
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("DragViewLog", " long " + position);
            //记录长按item位置
            preDraggedOverPosition = position;
            //获取被长按item的drawing cache
            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(true);
            //通过被长按item，获取拖动item的bitmap
            Bitmap dragBitmap = Bitmap.createBitmap(view.getDrawingCache());
            //设置拖动item的参数
            dragImageViewParams.gravity = Gravity.TOP | Gravity.LEFT;
            //设置拖动item为原item 1.2倍
            dragImageViewParams.width = (int) (AMP_FACTOR * dragBitmap.getWidth());
            dragImageViewParams.height = (int) (AMP_FACTOR * dragBitmap.getHeight());
            //设置触摸点为绘制拖动item的中心
            dragImageViewParams.x = (downRawX - dragImageViewParams.width / 2);
            dragImageViewParams.y = (downRawY - dragImageViewParams.height / 2);
            dragImageViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            dragImageViewParams.format = PixelFormat.TRANSLUCENT;
            dragImageViewParams.windowAnimations = 0;
            //dragImageView为被拖动item的容器，清空上一次的显示
            if ((int) dragImageView.getTag() == DRAG_IMG_SHOW) {
                windowManager.removeView(dragImageView);
                dragImageView.setTag(DRAG_IMG_NOT_SHOW);
            }

            //设置本次被长按的item
            dragImageView.setImageBitmap(dragBitmap);

            //添加拖动item到屏幕
            windowManager.addView(dragImageView, dragImageViewParams);
            dragImageView.setTag(DRAG_IMG_SHOW);
            isViewOnDrag = true;

            //设置被长按item不显示
            ((DragAdapter) getAdapter()).hideView(position);
            return true;
        }

    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //被按下时记录按下的坐标
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //获取触摸点相对于屏幕的坐标
            downRawX = (int) ev.getRawX();
            downRawY = (int) ev.getRawY();
        }
        //dragImageView处于被拖动时，更新dragImageView位置
        else if ((ev.getAction() == MotionEvent.ACTION_MOVE) && (isViewOnDrag)) {
            /*if (clicks == 0) {
                downRawX = (int) ev.getRawX();
                downRawY = (int) ev.getRawY();
            }*/
            ++clicks;
            Log.i(TAG, "" + ev.getRawX() + " " + ev.getRawY());
            //设置触摸点为dragImageView中心
            dragImageViewParams.x = (int) (ev.getRawX() - dragImageView.getWidth() / 2);
            dragImageViewParams.y = (int) (ev.getRawY() - dragImageView.getHeight() / 2);
            //更新窗口显示
            windowManager.updateViewLayout(dragImageView, dragImageViewParams);
            //获取当前触摸点的item position
            int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
            //如果当前停留位置item不等于上次停留位置的item，交换本次和上次停留的item
            if ((currDraggedPosition != AdapterView.INVALID_POSITION) && (currDraggedPosition != preDraggedOverPosition)) {
                ((DragAdapter) getAdapter()).swapView(preDraggedOverPosition, currDraggedPosition);
                preDraggedOverPosition = currDraggedPosition;
            }
        }
        //释放dragImageView
        else if ((ev.getAction() == MotionEvent.ACTION_UP) && (isViewOnDrag)) {
            ((DragAdapter) getAdapter()).showHideView();
            if ((int) dragImageView.getTag() == DRAG_IMG_SHOW) {
                windowManager.removeView(dragImageView);
                dragImageView.setTag(DRAG_IMG_NOT_SHOW);
            }
            isViewOnDrag = false;
            clicks = 0;
        }


        return super.onTouchEvent(ev);
    }


}
