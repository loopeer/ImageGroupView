package com.loopeer.android.librarys.imagegroupview.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.OnImageClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupSavedState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ImageGridView extends GridView implements GridImageAdapter.OnSquareClickListener {
    private static final String TAG = "ImageGridView";

    private final static int MAX_VALUE = -1;

    private ImageGroupSavedState imageGroupSavedState;
    private List<SquareImage> preImages;
    private OnImageClickListener clickListener;
    private boolean mShowAddButton, mRoundAsCircle;
    private int mAddButtonDrawable;
    private int mPlaceholderDrawable;
    private int maxImageNum;
    private boolean mDragDismiss;
    private boolean mDoUploadShowDialog;
    private GridImageAdapter mGridImageAdapter;

    public ImageGridView(Context context) {
        this(context, null);
    }

    public ImageGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        getAttrs(context, attrs, defStyle);

        init();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
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
        mGridImageAdapter = new GridImageAdapter(getContext(), this);
        setAdapter(mGridImageAdapter);
        updateImages();
    }


    private void updateImages() {
        mGridImageAdapter.updateData(preImages, mShowAddButton && (getCanSelectMaxNum() != 0 || maxImageNum == MAX_VALUE));
        mGridImageAdapter.updateParam(mAddButtonDrawable, mPlaceholderDrawable, mRoundAsCircle);
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

    public void setGridImageAdapter(GridImageAdapter gridImageAdapter) {
        mGridImageAdapter = gridImageAdapter;
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
            NavigatorImage.INSTANCE.startImageSwitcherActivity(getContext(), getSquarePhotos(), position,
                    mShowAddButton, mPlaceholderDrawable, getId(), mDragDismiss);
        }
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
                                        NavigatorImage.INSTANCE.startCustomAlbumActivity(getContext(), getCanSelectMaxNum(), getId(), AlbumActivity.Companion.getTAKE_PHOTO());
                                    } else {
                                        NavigatorImage.INSTANCE.startCustomAlbumActivity(getContext(), getCanSelectMaxNum(), getId(), AlbumActivity.Companion.getALBUM());
                                    }
                                }
                            })
                    .show();
        } else {
            NavigatorImage.INSTANCE.startCustomAlbumActivity(getContext(), getCanSelectMaxNum(), getId());
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled() || !isClickable()) return false;
        if (pointToPosition((int) ev.getX(), (int) ev.getY()) == -1 && ev.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
}