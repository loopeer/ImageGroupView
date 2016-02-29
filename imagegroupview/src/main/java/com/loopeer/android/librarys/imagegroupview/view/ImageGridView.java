package com.loopeer.android.librarys.imagegroupview.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.OnImageClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupSavedState;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ImageGridView extends GridView implements GridImageAdapter.OnSquareClickListener {
    private final static int MAX_VALUE = -1;

    private String unionKey;
    private ImageGroupSavedState imageGroupSavedState;
    private List<SquareImage> preImages;
    private OnImageClickListener clickListener;
    private int mAddButtonDrawable;
    private int mPlaceholderDrawable;
    private boolean mShowAddButton, mRoundAsCircle;
    private int maxImageNum;
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

        a.recycle();
    }

    private void init() {
        preImages = new ArrayList<>();
        mGridImageAdapter = new GridImageAdapter(getContext(), this);
        setAdapter(mGridImageAdapter);
        updateImages();
    }

    private void updateImages() {
        mGridImageAdapter.updateData(preImages, mShowAddButton && (getCanSelectMaxNum() != 0 || maxImageNum == MAX_VALUE));
        mGridImageAdapter.updateParam(mAddButtonDrawable, mPlaceholderDrawable, mRoundAsCircle);
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

    public void setNetworkPhotosWithKey(ArrayList<String> urls) {
        if (urls == null) return;
        preImages.clear();
        for (String url : urls) {
            String[] headWithKey = url.split("/");
            preImages.add(new SquareImage(null, url, headWithKey[headWithKey.length - 1], SquareImage.PhotoType.NETWORK));
        }
        updateImages();
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
            NavigatorImage.startImageSwitcherActivity(getContext(), getSquarePhotos(), position,
                    mShowAddButton, mPlaceholderDrawable, getId());
        }
    }

    private List<SquareImage> getSquarePhotos() {
        return preImages;
    }

    private void doUpLoadPhotoClick() {
        NavigatorImage.startCustomAlbumActivity(getContext(), getCanSelectMaxNum(), getId());
    }

    private int getCanSelectMaxNum() {
        if (maxImageNum == MAX_VALUE) return 0;
        return maxImageNum - preImages.size();
    }

    public void onParentResult(int requestCode, Intent data) {
        if (data == null || data.getIntExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, 0) != getId()) return;
        List<String> images = data.getStringArrayListExtra(NavigatorImage.EXTRA_PHOTOS_URL);
        ArrayList<Integer> positions = data.getIntegerArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION);
        if (requestCode == NavigatorImage.RESULT_IMAGE_SWITCHER && null != positions) {
            doPhotosDelete(positions);
        } else if (requestCode == NavigatorImage.RESULT_SELECT_PHOTOS && null != images) {
            doSelectPhotos(images);
        }
    }

    public void setUnionKey(String key) {
        unionKey = key;
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

    private void doSelectPhotos(List<String> images) {
        for (String url : images) {
            doSelectPhotosByUrl(url);
        }
    }

    private void doSelectPhotosByUrl(String url) {
        refreshPhotoView(url);
    }

    private void refreshPhotoView(String url) {
        SquareImage squareImage = new SquareImage(url, null, getPhotoKey(), SquareImage.PhotoType.LOCAL);
        preImages.add(squareImage);
        updateImages();
    }

    @NonNull
    private String getPhotoKey() {
        return "image_" + unionKey + "_" + System.currentTimeMillis();
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
            preImages.addAll(getSquarePhotos());
            updateImages();
            imageGroupSavedState = null;
        }
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        clickListener = listener;
    }
}