package com.loopeer.android.librarys.imagegroupview.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import java.util.List;

public class ImageGridView extends GridView implements GridImageAdapter.OnSquareClickListener {
    private final static int COLUMN = 3;
    private final static int CHILD_MARGIN = 4;
    private final static int MAX_VALUE = -1;

    private ArrayList<Integer> mPhotoViewIDs;
    private FragmentManager mManager;
    private String unionKey;
    private ImageGroupSavedState imageGroupSavedState;
    private Uri preTakePhotoUri;
    private List<SquareImage> preImages;
    private OnImageClickListener clickListener;
    private int addButtonDrawable;
    private int deleteDrawable;
    private int placeholderDrawable;

    private boolean mShowAddButton, roundAsCircle;
    private int childMargin;
    private int maxImageNum;
    private int column;
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
        roundAsCircle = false;
        maxImageNum = a.getInteger(R.styleable.ImageGroupView_maxImageNum, MAX_VALUE);
        childMargin = a.getDimensionPixelSize(R.styleable.ImageGroupView_childMargin, CHILD_MARGIN);
        column = a.getInteger(R.styleable.ImageGroupView_column, COLUMN);
        addButtonDrawable = a.getResourceId(R.styleable.ImageGroupView_addButtonDrawable, R.drawable.ic_photo_default);
        deleteDrawable = a.getResourceId(R.styleable.ImageGroupView_deleteDrawable, R.drawable.ic_delete);
        placeholderDrawable = a.getResourceId(R.styleable.ImageGroupView_imagePlaceholderDrawable, R.drawable.ic_image_default);

        a.recycle();
    }

    private void init() {
        mPhotoViewIDs = new ArrayList<>();
        preImages = new ArrayList<>();
        mGridImageAdapter = new GridImageAdapter(getContext(), this);
        setAdapter(mGridImageAdapter);
    }

    private void updateImages() {
        mGridImageAdapter.updateData(preImages, mShowAddButton);
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
            //clickListener.onImageClick(view.getSquareImage(), getSquarePhotos(), getInternetUrls());
        } else {
            NavigatorImage.startImageSwitcherActivity(getContext(), getSquarePhotos(), position, mShowAddButton, placeholderDrawable);
        }
    }

    private List<SquareImage> getSquarePhotos() {
        return preImages;
    }

    private void doUpLoadPhotoClick() {
        NavigatorImage.startCustomAlbumActivity(getContext(), getCanSelectMaxNum());
    }

    private int getCanSelectMaxNum() {
        if (maxImageNum == MAX_VALUE) return 0;
        return maxImageNum - mPhotoViewIDs.size() + 1;
    }

    public void onParentResult(int requestCode, Intent data) {
        List<String> images = data.getStringArrayListExtra(NavigatorImage.EXTRA_PHOTOS_URL);
        ArrayList<Integer> positions = data.getIntegerArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL_POSITION);
        if (requestCode == NavigatorImage.RESULT_IMAGE_SWITCHER && null != positions) {
            doPhotosDelete(positions);
        } else if (requestCode == NavigatorImage.RESULT_SELECT_PHOTOS && null != images) {
            doSelectPhotos(images);
        }
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

}