package com.loopeer.android.librarys.imagegroupview.view;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.OnImageClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupSavedState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DragGridView extends GridView implements GridDragAdapter.OnSquareClickListener, GridDragAdapter.OnSquareLongClickListener {
    private static final String TAG = "ImageGridView";

    private boolean isDrag = false;//是否是在拖动
    private int downX, downY;//点击时的X和Y位置
    private int windowX, windowY;//点击时对应整个界面的X和Y位置
    private int win_view_x, win_view_y;//屏幕上的x、y位置
    private int dragOffsetX, dragOffsetY;//拖动的x、y距离
    private int dragPosition;//长按时的position
    private int dropPosition;//up后item对应的position
    private int startPosition;//开始拖动时的item的position
    private int itemHeight, itemWidth;//item的高和宽
    private View dragImageView = null;//拖动时对应的item的view
    private View dragItemView = null;//长按时对应的item的view
    private WindowManager windowManager = null;//windowManager管理器
    private WindowManager.LayoutParams windowParams = null;//需要拖动的镜像
    private int itemTotalCount;//item总量
    private int nColumns = 3;//一行item数
    private int nRows;//行数
    private int remainder;//剩余部分
    private boolean isMoving = false;//是否在移动
    private int holdPosition;//需要移动的position
    private double dragScale = 1.2d;//拖动时的放大倍数
    //    private Vibrator mVibrator;//振动器
    private int mHorizontalSpacing = 15;//item间水平间距
    private int mVerticalSpacing = 15;//item间垂直间距
    private String LastAnimationID;//移动时候最后动画的id
    private RelativeLayout rootLayout;//执行动画的布局


    private final static int MAX_VALUE = -1;

    private ImageGroupSavedState imageGroupSavedState;
    private List<SquareImage> preImages;
    private OnImageClickListener clickListener;
    private int mAddButtonDrawable;
    private int mPlaceholderDrawable;
    private boolean mShowAddButton, mRoundAsCircle;
    private int maxImageNum;
    private boolean mDragDismiss;
    private boolean mDoUploadShowDialog;
    private GridDragAdapter mGridDragAdapter;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        getAttrs(context, attrs, defStyle);

        init();
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 判断是否点击在指定view中
     *
     * @param view
     * @param event
     * @return
     */
    private boolean isRangeOfView(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);//返回左、上位置
        int downX = (int) event.getX();
        int downY = (int) event.getY();
        int x = location[0];
//        int y = location[1] - getStatusBarHeight() - ImagePickerActivity.StruesHeight;//这里需要减去状态栏的高度和导航栏的高度，否则座标会存在偏差
        int y = location[1] - getStatusBarHeight();//这里需要减去状态栏的高度否则座标会存在偏差
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        return !(downX < x || downX > (x + viewWidth) || downY < y || downY > (y + viewHeight));

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) ev.getX();
            downY = (int) ev.getY();//获取点击的x和y
            windowX = (int) ev.getX();
            windowY = (int) ev.getY();

            int position = pointToPosition(downX, downY);//返回点下的位置
            if (position != AdapterView.INVALID_POSITION) {
                //获取当前点击的view
                View view = getChildAt(position - getFirstVisiblePosition());
                if (isDrag) {
                    Log.d("DragGridLog", "已点击");
                } else {
                    //实现长按item的操作
                    setOnClickListener(ev);
                }


            }
        }

        return super.onInterceptTouchEvent(ev);
    }


    /**
     * 删除item
     *
     * @param position
     */
    public void deleteInfo(int position) {

    }

    /**
     * 这里需要获取gridview最外层的布局，因为我们无法直接去对item进行动画，所以需要创建一个镜像，对镜像进行动画操作
     */
    public void setRelativeLayout(RelativeLayout layout) {
        //TODO
        this.rootLayout = layout;
    }

    /**
     * 删除动画，先不做
     *
     * @param position
     */
    public void deleteAnimation(final int position) {

    }

    /**
     * 平移动画
     *
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    public AnimatorSet createAnimator(View view, float startX, float endX,
                                      float startY, float endY) {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        AnimatorSet animatorSetXY = new AnimatorSet();
        animatorSetXY.playTogether(animatorX, animatorY);
        return animatorSetXY;
    }

    /**
     * 这是viewGroup的onTouch事件，这个事件在onInterceptTouchEvent没有向下分发，自己来处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (dragImageView != null && dragPosition != AdapterView.INVALID_POSITION) {
            //移动时对应的x、y位置
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) ev.getX();
                    windowX = (int) ev.getX();
                    windowY = (int) ev.getY();
                    downY = (int) ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //进行拖动时改变其位置
                    onDrag(x, y, (int) ev.getRawX(), (int) ev.getRawY());
                    if (!isMoving) {
                        onMove(x, y);
                    }
                    //如果移动的位置没有item，则跳出?
                    if (pointToPosition(x, y) != AdapterView.INVALID_POSITION) {
                        break;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    onDrop(x, y);
                    requestDisallowInterceptTouchEvent(false);
                    break;
                default:
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        stopDrag();
        isDrag = false;
        GridDragAdapter mGridDragAdapter = (GridDragAdapter) getAdapter();
//        mDragAdapter.setisDelete(false);
        mGridDragAdapter.notifyDataSetChanged();
    }

    /**
     * 松手的情况
     *
     * @param x
     * @param y
     */
    private void onDrop(int x, int y) {
        //根据拖动item下方的坐标获取对应的item的position
        int tempPosition = pointToPosition(x, y);
        dropPosition = tempPosition;
        GridDragAdapter mGridDragAdapter = (GridDragAdapter) getAdapter();
        //显示刚拖动的item
//        mGridDragAdapter.setShowDropItem(true);
        //刷新适配器
        mGridDragAdapter.notifyDataSetChanged();
    }

    /**
     * 在拖动情况下用到了android窗口机制 http://www.jianshu.com/p/40a9c93b5a8d
     *
     * @param x
     * @param y
     * @param rawX
     * @param rawY
     */
    private void onDrag(int x, int y, int rawX, int rawY) {
        if (dragImageView != null) {
            //透明度
            windowParams.alpha = 0.6f;
            //显示坐标
            windowParams.x = rawX - win_view_x;
            windowParams.y = rawY - win_view_y;
            //对window进行更新
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
    }

    /**
     * 长点击事件
     *
     * @param ev
     */
    private void setOnClickListener(final MotionEvent ev) {
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                isDrag = true;
                GridDragAdapter mGridDragAdapter = (GridDragAdapter) getAdapter();
//                mGridDragAdapter.setisDelete(true);
                mGridDragAdapter.notifyDataSetChanged();
                startPosition = position;//第一次点击的position
                dragPosition = position;
                //最后一个加号，另作处理
                ViewGroup dropViewGroup = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                //......
                return false;
            }
        });
    }


    /**
     * @param dragBitmap
     * @param x
     * @param y
     */
    private void startDrag(Bitmap dragBitmap, int x, int y) {
        stopDrag();
        windowParams = new WindowManager.LayoutParams();//获取window界面
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;//Gravity.TOP|Gravity.LEFT;这个必须加
        windowParams.x = x - win_view_x;
        windowParams.y = y - win_view_y;//得到preview相对于左上角的坐标

        //设置拖拽item的宽和高
        windowParams.width = (int) (dragScale * dragBitmap.getWidth());
        windowParams.height = (int) (dragScale * dragBitmap.getHeight());
        this.windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;//
        this.windowParams.format = PixelFormat.TRANSLUCENT;//半透明
        this.windowParams.windowAnimations = 0;
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(dragBitmap);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);//
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }

    /**
     * 停止拖动，释放并初始化
     */
    private void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }

    }

    /**
     * 所以要进行计算高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /**
     * 隐藏放下的item
     */
    private void hideDropItem() {
//        ((GridDragAdapter)getAdapter()).setShowDropItem(false);
    }

    public Animation getMoveAnimation(float toXValue, float toYValue) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, toXValue,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, toYValue
        );//当前位置移动到指定位置
        translateAnimation.setFillAfter(true);//设置一个动画执行完之后，View对象保留在终止位置
        translateAnimation.setDuration(300L);
        return translateAnimation;
    }

    /**
     * 移动时触发，涉及到算法
     *
     * @param x
     * @param y
     */
    private void onMove(int x, int y) {

        int dPosition = pointToPosition(x, y);//拖动的view下方的position
        //TODO 判断下方position是否时不能拖动的
        if (dPosition < getCount() - 1) {
            if ((dPosition == -1) || (dPosition == dragPosition)) {
                return;
            }
            dropPosition = dPosition;
            if (dragPosition != startPosition) {
                dragPosition = startPosition;
            }
            int moveCount;
            //拖动的==开始拖的||拖动的！=放下的
            if (dragPosition == startPosition || dragPosition != dropPosition) {
                //需要移动的item数量
                moveCount = dropPosition - dragPosition;
            } else {
                moveCount = 0;
            }
            if (moveCount == 0) {
                return;
            }

            int moveCount_abs = Math.abs(moveCount);
            if (dPosition != dragPosition){
                //dragGroup设置为不可见
                ViewGroup dragGroup = (ViewGroup) getChildAt(dragPosition);
                dragGroup.setVisibility(View.INVISIBLE);
                float to_x = 1;//当前下方position
                float to_y;
            }

        }

    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageGroupView, defStyleAttr, 0);
        if (a == null) return;

        mShowAddButton = a.getBoolean(R.styleable.ImageGroupView_showAddButton, false);
        mRoundAsCircle = false;
        maxImageNum = a.getInteger(R.styleable.ImageGroupView_maxImageNum, MAX_VALUE);
        mAddButtonDrawable = a.getResourceId(R.styleable.ImageGroupView_addButtonDrawable, R.drawable.ic_photo_default);
        mPlaceholderDrawable = a.getResourceId(R.styleable.ImageGroupView_imagePlaceholderDrawable, R.drawable.ic_image_default);
        mDragDismiss = a.getBoolean(R.styleable.ImageGroupView_dragDismiss, true);
        mDoUploadShowDialog = a.getBoolean(R.styleable.ImageGroupView_showDialog, false);

        a.recycle();
    }

    private void init() {
        /*setEnabled(true);
        setClickable(true);*/
        preImages = new ArrayList<>();
        mGridDragAdapter = new GridDragAdapter(getContext(), this, this);
        setAdapter(mGridDragAdapter);
        updateImages();
    }


    private void updateImages() {
        mGridDragAdapter.updateData(preImages, mShowAddButton && (getCanSelectMaxNum() != 0 || maxImageNum == MAX_VALUE));
        mGridDragAdapter.updateParam(mAddButtonDrawable, mPlaceholderDrawable, mRoundAsCircle);
    }

    private void updateImagesPosition() {
        int[] position = new int[2];
        this.getLocationOnScreen(position);

        int columnWidth = getColumnWidth();
        int numColumns = getNumColumns();
        int horizontalSpacing = getHorizontalSpacing();
        int verticalSpacing = getVerticalSpacing();
        int left, top;
        for (int i = 0; i < preImages.size(); i++) {
            left = position[0] + getPaddingLeft() + i % numColumns * columnWidth + i % numColumns * horizontalSpacing;
            top = position[1] + getPaddingTop() - DisplayUtils.getStatusBarHeight(getContext()) + i / numColumns * columnWidth + i / numColumns * verticalSpacing;
            preImages.get(i).setPosition(left, top, columnWidth, columnWidth);
        }
    }

    public void setGridDragAdapter(GridDragAdapter GridDragAdapter) {
        mGridDragAdapter = GridDragAdapter;
        updateImages();
    }

    public void updateNetPhotos(List<String> photos) {
        if (photos == null) return;
        preImages.clear();
        for (String url : photos) {
            SquareImage squareImage = new SquareImage(null, url, null, SquareImage.PhotoType.NETWORK);
            squareImage.setId(createIndex());
            preImages.add(squareImage);
        }
        updateImages();
    }

    public void setNetworkPhotosWithKey(List<String> urls) {
        if (urls == null) return;
        preImages.clear();
        for (String url : urls) {
            String[] headWithKey = url.split("/");
            preImages.add(new SquareImage(null, url, headWithKey[headWithKey.length - 1], SquareImage.PhotoType.NETWORK));
        }
        updateImages();
    }

    public void setNetworkPhotosWithKey(List<String> urls, List<String> keys) {
        if (urls == null || keys == null || urls.size() != keys.size()) {
            return;
        }
        for (int i = 0; i < urls.size(); i++) {

        }
    }

    public void updateLocalPhotos(List<String> photos) {
        if (photos == null) return;
        preImages.clear();
        for (String url : photos) {
            SquareImage squareImage = new SquareImage(url, null, null, SquareImage.PhotoType.LOCAL);
            squareImage.setId(createIndex());
            preImages.add(squareImage);
        }
        updateImages();
    }

    public void updateImage(List<SquareImage> photos) {
        if (photos == null) return;
        preImages.clear();
        for (SquareImage squareImage : photos) {
            squareImage.setId(createIndex());
            preImages.add(squareImage);
        }
        updateImages();
    }

    public void showAddButton(boolean showAddButton) {
        mShowAddButton = showAddButton;
        updateImages();
    }

    @Override
    public void photoClick(View v, SquareImage squareImage, int position) {
        if (squareImage == null) {
            doUpLoadPhotoClick();
        } else if (clickListener != null) {
            clickListener.onImageClick(v, squareImage);
        } else {
            updateImagesPosition();
            NavigatorImage.startImageSwitcherActivity(getContext(), getSquarePhotos(), position,
                    mShowAddButton, mPlaceholderDrawable, getId(), mDragDismiss);
        }
    }

    @Override
    public void photoLongClick(View v, SquareImage squareImage, int position) {
        //区分ImagePickerActivity和其他不需要长点击的页面
        Log.d("ImageGridViewLog", "onLongClick" + position);
//        v.setTranslationY(300);

    }


    private List<SquareImage> getSquarePhotos() {
        return preImages;
    }

    private void doUpLoadPhotoClick() {
        if (mDoUploadShowDialog) {
            new AlertDialog.Builder(getContext())
                    .setItems(new String[]{getContext().getString(R.string.take_photo),
                                    getContext().getString(R.string.select_images)},
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        NavigatorImage.startCustomAlbumActivity(getContext(), getCanSelectMaxNum(), getId(), AlbumActivity.TAKE_PHOTO);
                                    } else {
                                        NavigatorImage.startCustomAlbumActivity(getContext(), getCanSelectMaxNum(), getId(), AlbumActivity.ALBUM);
                                    }
                                }
                            })
                    .show();
        } else {
            NavigatorImage.startCustomAlbumActivity(getContext(), getCanSelectMaxNum(), getId());
        }
    }

    private int getCanSelectMaxNum() {
        if (maxImageNum == MAX_VALUE) return 0;
        return maxImageNum - preImages.size();
    }

    public void onParentResult(int requestCode, Intent data) {
        if (data == null || data.getIntExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, 0) != getId())
            return;
        List<Image> images = (List<Image>) data.getSerializableExtra(NavigatorImage.EXTRA_PHOTOS_URL);
        ArrayList<Integer> positions = data.getIntegerArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION);
        if (requestCode == NavigatorImage.RESULT_IMAGE_SWITCHER && null != positions) {
            doPhotosDelete(positions);
        } else if (requestCode == NavigatorImage.RESULT_SELECT_PHOTOS && null != images) {
            doSelectPhotos(images);
        }
    }

    public void onParentResults(int requestCode, Intent data) {
        if (data == null)
            return;
        List<Image> images = (List<Image>) data.getSerializableExtra(NavigatorImage.EXTRA_PHOTOS_URL);
        ArrayList<Integer> positions = data.getIntegerArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION);
        if (requestCode == NavigatorImage.RESULT_IMAGE_SWITCHER && null != positions) {
            doPhotosDelete(positions);
        } else if (requestCode == NavigatorImage.RESULT_SELECT_PHOTOS && null != images) {
            doSelectPhotos(images);
        }
    }

    public ArrayList<String> getImageKeys() {
        ArrayList<String> result = new ArrayList<>();
        for (SquareImage squareImage : preImages) {
            if (!TextUtils.isEmpty(squareImage.interNetUrl) || !TextUtils.isEmpty(squareImage.localUrl)) {
                result.add(squareImage.urlKey);
            }
        }
        return result;
    }

    public HashMap<String, String> getUploadKeyUrlMap() {
        HashMap<String, String> map = new HashMap<>();
        for (SquareImage squareImage : preImages) {
            if (TextUtils.isEmpty(squareImage.interNetUrl) && !TextUtils.isEmpty(squareImage.localUrl)) {
                map.put(squareImage.urlKey, squareImage.localUrl);
            }
        }
        return map;
    }

    public String getImageKeyString() {
        ArrayList<String> keys = getImageKeys();
        if (keys.isEmpty()) return null;
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(key);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public ArrayList<String> getLocalUrls() {
        ArrayList<String> result = new ArrayList<>();
        for (SquareImage squareImage : preImages) {
            if (TextUtils.isEmpty(squareImage.interNetUrl) && !TextUtils.isEmpty(squareImage.localUrl)) {
                result.add(squareImage.localUrl);
            }
        }
        return result;
    }

    public ArrayList<String> getInternetUrls() {
        ArrayList<String> result = new ArrayList<>();
        for (SquareImage squareImage : preImages) {
            if (!TextUtils.isEmpty(squareImage.interNetUrl)) {
                result.add(squareImage.interNetUrl);
            }
        }
        return result;
    }

    private void doPhotosDelete(final ArrayList<Integer> positions) {
        if (positions == null) return;
        List<SquareImage> deleteImages = new ArrayList<>();
        for (int position : positions) {
            deleteImages.add(preImages.get(position));
        }
        for (SquareImage squareImage : deleteImages) {
            if (preImages.contains(squareImage)) preImages.remove(squareImage);
        }
        updateImages();
    }

    private void doSelectPhotos(List<Image> images) {
        for (Image image : images) {
            SquareImage squareImage = new SquareImage(image.url, null, getPhotoKey(image.time), SquareImage.PhotoType.LOCAL);
            preImages.add(squareImage);
        }
        updateImages();
    }

    @NonNull
    private String getPhotoKey(long time) {
        return "image_" + time;
    }

    private int createIndex() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("mmss");
        String date = sdf.format(new Date(time));
        int num1 = Integer.valueOf(date + Long.toString(time % 1000));
        int i = num1 * 10 + preImages.size();
        return i;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable parcelable = super.onSaveInstanceState();
        final ImageGroupSavedState imageSaveState = new ImageGroupSavedState(parcelable);
        imageSaveState.setSquarePhotos(getSquarePhotos());
        return imageSaveState;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (!(state instanceof ImageGroupSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        final ImageGroupSavedState ss = (ImageGroupSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        imageGroupSavedState = ss;
        restoreView();
    }

    private void restoreView() {
        if (imageGroupSavedState != null) {
            preImages.clear();
            preImages.addAll(imageGroupSavedState.getSquarePhotos());
            updateImages();
            imageGroupSavedState = null;
        }
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        clickListener = listener;
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isClickable()) return false;
        if (pointToPosition((int) ev.getX(), (int) ev.getY()) == -1 && ev.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("ImageGridViewLog", " action_down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("ImageGridViewLog", " action_move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("ImageGridViewLog", " action_up");
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }*/

}
