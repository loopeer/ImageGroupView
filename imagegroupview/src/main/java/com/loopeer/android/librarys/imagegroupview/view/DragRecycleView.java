package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

public class DragRecycleView extends RecyclerView {
    private static final int DRAG_IMG_SHOW = 1;//滑动状态
    private static final int DRAG_IMG_NOT_SHOW = 0;//非滑动状态
    private static final float AMP_FACTOR = 1.2f;
    private static final String TAG = "DragRecycleView";

    private ImageView dragImageView;//滑动的imageView
    private WindowManager.LayoutParams dragImageViewParams;
    private WindowManager windowManager;
    private boolean isViewOnDrag = false;

    public TextView tv;

    private int preDraggedOverPosition = AdapterView.INVALID_POSITION;//滑动前的位置
    private int downRawX;
    private int downRawY;
    private int sHeight;


    public DragRecycleView(Context context) {
        this(context, null);
    }

    public DragRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        //初始化显示被拖动item的imageView
        //初始化用于设置dragImageView的参数对象
//        View.inflate(context, R.layout.drag_recycle, this);
//        tv = (TextView) findViewById(R.id.delete);
        dragImageView = new ImageView(getContext());
        dragImageView.setTag(DRAG_IMG_NOT_SHOW);
        dragImageViewParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);


        sHeight = wm.getDefaultDisplay().getHeight();
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent ev) {
        DragRecycleAdapter adapter = (DragRecycleAdapter) getAdapter();
*//*        adapter.setOnItemClickListener(new DragRecycleAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Log.d("ImagePickerActivityLog", "click" + pos);
            }
        })*//*;
        *//*adapter.setOnItemLongClickListener(new DragRecycleAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int pos) {
                preDraggedOverPosition = pos;
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
                ((DragRecycleAdapter) getAdapter()).hideView(pos);
                Log.d("ImagePickerActivityLog", "longClick" + pos);
            }
        })*//*;
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //获取触摸点相对于屏幕的坐标
            downRawX = (int) ev.getRawX();
            Log.d("ImagePickerActivityLog", "longClick" + downRawX);
            downRawY = (int) ev.getRawY();
            Log.d("ImagePickerActivityLog", "longClick" + downRawY);
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
            int mFirstPosition = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

            View targetView = this.findChildViewUnder((int) ev.getX(), (int) ev.getY());
            */

    /**
     * //http://dalufan.com/2016/12/02/android-recycleview-useage-issues/在AbsListView（listview、gridview）中，有个根据屏幕位置获取pos的方法：pointToPosition，但在recycleview中没有这个方法，可以通过下面方法来达到相同的效果
     *//*
            int currDraggedPosition = getChildAdapterPosition(targetView);
            Log.d("DragRecyleViewLog ", " " + currDraggedPosition);
            //如果当前停留位置item不等于上次停留位置的item，交换本次和上次停留的item
            if ((currDraggedPosition != AdapterView.INVALID_POSITION) && (currDraggedPosition != preDraggedOverPosition)) {
                ((DragRecycleAdapter) getAdapter()).swapView(preDraggedOverPosition, currDraggedPosition);
                preDraggedOverPosition = currDraggedPosition;
            }
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
    }*/
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        DragRecycleAdapter adapter = (DragRecycleAdapter) getAdapter();
        if (e.getAction() == MotionEvent.ACTION_DOWN) {

        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            tv.setVisibility(VISIBLE);
            if (sHeight - e.getY() < 400) {
                tv.setText("松手即可删除");
            }else{tv.setText("拖动到此处删除");}
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            View targetView = this.findChildViewUnder((int) e.getX(), (int) e.getY());
            int currDraggedPosition = getChildAdapterPosition(targetView);//http://dalufan.com/2016/12/02/android-recycleview-useage-issues/在AbsListView（listview、gridview）中，有个根据屏幕位置获取pos的方法：pointToPosition，但在recycleview中没有这个方法，可以通过下面方法来达到相同的效果
            if (sHeight - e.getY() < 400) {
                adapter.removeView(currDraggedPosition);
            }
            tv.setVisibility(GONE);
        }
        return super.onTouchEvent(e);
    }

}
