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
