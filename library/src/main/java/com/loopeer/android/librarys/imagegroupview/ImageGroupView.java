package com.loopeer.android.librarys.imagegroupview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImageGroupView extends LinearLayout {

    private final static int COLUMN = 3;
    private final static int CHILD_MARGIN = 4;
    private final static int MAX_VALUE = -1;
    private LinearLayout mLayoutItem;
    private ArrayList<Integer> mPhotoViewIDs;
    private FragmentManager mManager;
    private ImageGroupSavedState imageGroupSavedState;
    private Uri preTakePhotoUri;
    private ArrayList<String> preImageUrls;
    private OnImageClickListener clickListener;
    private int mPhotoIsDoingId;
    private int addButtonDrawable;
    private int deleteDrawable;
    private int placeholderDrawable;

    private boolean showAddButton;
    private int childMargin;
    private int maxImageNum;
    private int column;

    public ImageGroupView(Context context) {
        this(context, null);
    }

    public ImageGroupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initData();
        getAttrs(context, attrs, defStyleAttr);
        setUpTreeObserver();
    }

    private void initData() {
        setOrientation(VERTICAL);
        mPhotoViewIDs = new ArrayList<>();
        preImageUrls = new ArrayList<>();
    }

    private void getAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageGroupView, defStyleAttr, 0);
        if (a == null) return;

        showAddButton = a.getBoolean(R.styleable.ImageGroupView_showAddButton, false);
        maxImageNum = a.getInteger(R.styleable.ImageGroupView_maxImageNum, MAX_VALUE);
        childMargin = a.getDimensionPixelSize(R.styleable.ImageGroupView_childMargin, CHILD_MARGIN);
        column = a.getInteger(R.styleable.ImageGroupView_column, COLUMN);
        addButtonDrawable = a.getResourceId(R.styleable.ImageGroupView_addButtonDrawable, R.drawable.ic_photo_default);
        deleteDrawable = a.getResourceId(R.styleable.ImageGroupView_deleteDrawable, R.drawable.ic_delete);
        placeholderDrawable = a.getResourceId(R.styleable.ImageGroupView_imagePlaceholderDrawable, R.drawable.ic_image_default);

    }

    private void setUpTreeObserver() {
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    updateViewWithDatas();
                    restoreView();
                }
            });
        }
    }

    private void restoreView() {
        if (imageGroupSavedState != null && showAddButton) {
            refreshLayout(imageGroupSavedState.getSquarePhotos());
            imageGroupSavedState = null;
        }
        if (preTakePhotoUri != null) {
            refreshPhotoView(preTakePhotoUri);
            preTakePhotoUri = null;
        }
    }

    private void initLayoutItem() {
        addNewLayoutItemWithoutTopMargin();
        addPhotoView();
    }

    private void addNewLayoutItemWithTopMargin() {
        addNewLayoutItem(true);
    }

    private void addNewLayoutItemWithoutTopMargin() {
        addNewLayoutItem(false);
    }

    private void addNewLayoutItem(boolean withTopMargin) {
        mLayoutItem = new LinearLayout(getContext());
        LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (withTopMargin) {
            lParams.topMargin = childMargin;
        }
        mLayoutItem.setLayoutParams(lParams);
        addView(mLayoutItem);
    }

    private void addPhotoView() {
        SquareImageView squareImageView = new SquareImageView(getContext());
        squareImageView.setImageResource(addButtonDrawable);
        int imageViewWidth = getImageWidth();
        squareImageView.setWidthByParent(imageViewWidth);
        squareImageView.setPlaceholderDrawable(placeholderDrawable);

        FrameLayout frame = new FrameLayout(getContext());
        LayoutParams frameParams = new LayoutParams(imageViewWidth, imageViewWidth);
        frame.addView(squareImageView, frameParams);
        LayoutParams layoutParams = new LayoutParams(imageViewWidth, imageViewWidth);
        if (mPhotoViewIDs.size() % column != 0) layoutParams.leftMargin = childMargin;
        if (mPhotoViewIDs.size() > 0 && mPhotoViewIDs.size() % column == 0) {
            addNewLayoutItemWithTopMargin();
        }
        mLayoutItem.addView(frame, layoutParams);

        final int squarePhotoViewId = createIndex();
        squareImageView.setId(squarePhotoViewId);
        mPhotoViewIDs.add(squarePhotoViewId);

        squareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SquareImageView view = (SquareImageView) v;
                if (isAddButton(view)) {
                    doPhotoClickSelectable(mPhotoViewIDs.size() - 1);
                } else if (clickListener != null) {
                    clickListener.onImageClick(view.getSquareImage(), getSquarePhotos(), getInternetUrls());
                }
            }
        });

        squareImageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (canClickToDelete(v)) {
                    return true;
                }
                doDeletePhoto(v);
                return true;
            }
        });
    }

    private boolean isAddButton(SquareImageView view) {
        return view.getImageLocalUrl() == null
                && view.getInternetUrl() == null;
    }

    private boolean canClickToDelete(View v) {
        return !showAddButton || (((SquareImageView) v).getImageLocalUrl() == null
                && ((SquareImageView) v).getInternetUrl() == null);
    }

    private int getImageWidth() {
        return (getMeasuredWidth() - childMargin * (column - 1) - getPaddingRight() - getPaddingLeft()) / column;
    }

    private void doDeletePhoto(View v) {
        final SquareImageView squarView = (SquareImageView) v;
        FrameLayout frame = (FrameLayout) squarView.getParent();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.RIGHT | Gravity.TOP);
        ImageView image = new ImageView(getContext());
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doPhotoDelete(squarView.getId());
            }
        });
        image.setImageResource(deleteDrawable);
        frame.addView(image, layoutParams);
    }

    private void doPhotoClickSelectable(final int photoId) {
        doUpLoadPhotoClick(photoId);
    }

    private void doPhotoDelete(final int photoId) {
        removeView(findViewById(photoId));
        mPhotoViewIDs.remove(new Integer(photoId));
        requestLayout();
        refresh();
    }

    private void refresh() {
        refreshLayout(getSquarePhotos());
    }

    private ArrayList<SquareImage> getSquarePhotos() {
        ArrayList<SquareImage> results = new ArrayList<>();
        for (Integer i : mPhotoViewIDs) {
            SquareImageView squareImageView = (SquareImageView) findViewById(i);
            if (squareImageView.getSquareImage() != null) results.add(squareImageView.getSquareImage());
        }
        return results;
    }

    private void refreshLayout(ArrayList<SquareImage> photos) {
        mPhotoViewIDs.clear();
        removeAllViews();
        initLayoutItem();
        for (int i = 0; i < photos.size(); i++) {
            SquareImageView squareImageView = (SquareImageView) findViewById(mPhotoViewIDs.get(mPhotoViewIDs.size() - 1));
            switch (photos.get(i).type) {
                case INTER:
                    squareImageView.setInternetData(photos.get(i).url);
                    break;
                case LOCAL:
                    squareImageView.setLocalUrl(photos.get(i).url);
                    break;
            }
            addPhoto(mPhotoViewIDs.get(mPhotoViewIDs.size() - 1));
        }
    }

    private void doUpLoadPhotoClick(int viewId) {
        photoClick(viewId);
        new GetImageDialogFragment.Builder(mManager)
                .setPositiveListener(new GetImageDialogFragment.ClickListener() {
                    @Override
                    public void click() {
                        String SDState = Environment.getExternalStorageState();
                        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
                            ActivityCompat.startActivityForResult((Activity) getContext(),
                                    new Intent(getContext(), UserCameraActivity.class), NavigatorImage.RESULT_TAKE_PHOTO,
                                    null);
                        } else {
                            Toast.makeText(getContext(), "内存卡不存在", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeListener(new GetImageDialogFragment.ClickListener() {
                    @Override
                    public void click() {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity) getContext()).startActivityForResult(i, NavigatorImage.RESULT_SELECT_PHOTO);
                    }
                })
                .show();
    }

    public void addPhoto(int viewId) {
        boolean isEndPhotoView = true;
        for (int i = 0; i < mPhotoViewIDs.size() - 1; i++) {
            if (mPhotoViewIDs.get(i) == viewId) {
                isEndPhotoView = false;
            }
        }
        if (isEndPhotoView && (maxImageNum == MAX_VALUE || mPhotoViewIDs.size() < maxImageNum)) {
            addPhotoView();
        }
    }

    public void setPhotos(ArrayList<String> photosNetUrl) {
        if (photosNetUrl == null) return;
        preImageUrls.addAll(photosNetUrl);
        updateViewWithDatas();
    }

    private void updateViewWithDatas() {
        if (showAddButton) {
            setPhotosWithButton(preImageUrls);
        } else {
            setPhotosWithoutButton(preImageUrls);
        }
    }

    private void setPhotosWithButton(ArrayList<String> photosNetUrl) {
        removeAllViews();
        mPhotoViewIDs.clear();
        initLayoutItem();
        int size = photosNetUrl != null ? photosNetUrl.size() : 0;
        for (int i = 0; i < size; i++) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(mPhotoViewIDs.get(mPhotoViewIDs.size() - 1));
            squareImageView.setInternetData(photosNetUrl.get(i));
            addPhoto(mPhotoViewIDs.get(mPhotoViewIDs.size() - 1));
        }
    }

    private void setPhotosWithoutButton(ArrayList<String> photosNetUrl) {
        removeAllViews();
        mPhotoViewIDs.clear();
        initLayoutItem();
        int size = photosNetUrl != null ? photosNetUrl.size() : 0;
        for (int i = 0; i < size; i++) {
            SquareImageView squareImageView = (SquareImageView) findViewById(mPhotoViewIDs.get(i));
            squareImageView.setInternetData(photosNetUrl.get(i));
            addPhotoWithoutButton(i, size);
        }
    }

    private void addPhotoWithoutButton(int position, int size) {
        if (position != size - 1) addPhotoView();
    }

    @SuppressWarnings("unused")
    public ArrayList<String> getLocalUrls() {
        ArrayList<String> map = new ArrayList<>();
        for (Integer i : mPhotoViewIDs) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(i);
            if (TextUtils.isEmpty(squareImageView.getInternetUrl()) && !TextUtils.isEmpty(squareImageView.getLocalUrl())) {
                map.add(squareImageView.getLocalUrl());
            }
        }
        return map;
    }

    @SuppressWarnings("unused")
    public ArrayList<String> getInternetUrls() {
        ArrayList<String> photos = new ArrayList<>();
        for (Integer i : mPhotoViewIDs) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(i);
            if (!TextUtils.isEmpty(squareImageView.getInternetUrl())) {
                photos.add(squareImageView.getInternetUrl());
            }
        }
        return photos;
    }

    @SuppressWarnings("unused")
    public boolean isEmpty() {
        boolean empty = true;
        for (Integer i : mPhotoViewIDs) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(i);
            if (squareImageView.getUploadImageKey() != null) {
                empty = false;
            }
        }
        return empty;
    }

    @SuppressWarnings("unused")
    public SquareImageView findFirstSquareView() {
        return (SquareImageView) this.findViewById(mPhotoViewIDs.get(0));
    }

    public void setFragmentManager(FragmentManager manager) {
        mManager = manager;
    }

    private int createIndex() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("mmss");
        String date = sdf.format(new Date(time));
        int num1 = Integer.valueOf(date + Long.toString(time % 1000));
        int i = num1 * 10 + mPhotoViewIDs.size();
        return i;
    }

    public void photoClick(int clickingViewId) {
        mPhotoIsDoingId = clickingViewId;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable parcelable = super.onSaveInstanceState();
        final ImageGroupSavedState imageSaveState = new ImageGroupSavedState(parcelable);
        imageSaveState.setDoingClickViewId(mPhotoIsDoingId);
        imageSaveState.setSquarePhotos(getSquarePhotos());
        return imageSaveState;
    }

    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (!(state instanceof ImageGroupSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        final ImageGroupSavedState ss = (ImageGroupSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mPhotoIsDoingId = ss.getDoingClickViewId();
        imageGroupSavedState = ss;
    }

    public void onParentResult(int requestCode, Intent data) {
        Uri imageSelectedUri = data.getData();
        String photoTakeurl = data.getStringExtra(NavigatorImage.EXTRA_PHOTO_URL);
        if (requestCode == NavigatorImage.RESULT_SELECT_PHOTO && null != imageSelectedUri) {
            doSelectImage(imageSelectedUri);
        } else if (requestCode == NavigatorImage.RESULT_TAKE_PHOTO && null != photoTakeurl) {
            doTakePhoto(photoTakeurl);
        }
    }

    private void doTakePhoto(String url) {
        Uri mUri = Uri.parse(url);
        refreshPhotoView(mUri);
    }

    private void doSelectImage(Uri data) {
        refreshPhotoView(data);
    }

    private void refreshPhotoView(Uri uri) {
        if (imageGroupSavedState != null) {
            preTakePhotoUri = uri;
            return;
        } else {
            preTakePhotoUri = null;
        }

        SquareImageView squarePhotoView = (SquareImageView) findViewById(mPhotoViewIDs.get(mPhotoIsDoingId));
        ImageGroupDisplayHelper.displayImage(squarePhotoView, uri, 200, 200);
        squarePhotoView.setFocusable(true);
        squarePhotoView.setFocusableInTouchMode(true);
        squarePhotoView.requestFocus();
        squarePhotoView.setLocalUrl(ImageGroupUtils.getPathOfPhotoByUri(getContext(), uri));
        addPhoto(squarePhotoView.getId());
    }

    @SuppressWarnings("unused")
    public void setOnImageClickListener(OnImageClickListener listener) {
        clickListener = listener;
    }

    public interface OnImageClickListener {
        void onImageClick(SquareImage clickImage, ArrayList<SquareImage> squareImages, ArrayList<String> allImageInternetUrl);
    }
}
