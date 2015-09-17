package com.loopeer.android.librarys.imagegroupview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Environment;
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
import java.util.HashMap;

public class ImageGroupView extends LinearLayout {

    private final static int COLUMN = 3;
    private final static int CHILD_MARGIN = 4;

    public interface PhotoGroupViewClickListener {
        void photoClick(int clickingViewId);
    }

    private LinearLayout mLayoutItem;
    private ArrayList<Integer> mPhotoViewIDs;
    private PhotoGroupViewClickListener mListener;
    private FragmentManager mManager;
    private boolean mClickToUpload = true;
    private boolean showAddButton = true;
    private int childMargin;
    private int maxImageNum = -1;
    private int column;

    private ArrayList<String> preImageUrls;

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
        maxImageNum = a.getInteger(R.styleable.ImageGroupView_maxImageNum, -1);
        childMargin = a.getDimensionPixelSize(R.styleable.ImageGroupView_childMargin, CHILD_MARGIN);
        column = a.getInteger(R.styleable.ImageGroupView_column, COLUMN);
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
                }
            });
        }
    }

    private void initLayoutItem() {
        mLayoutItem = new LinearLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mLayoutItem.setLayoutParams(layoutParams);
        addView(mLayoutItem);
        addPhotoView();
    }

    private void addPhotoView() {
        SquareImageView squareImageView = new SquareImageView(getContext());

        squareImageView.setImageResource(R.drawable.ic_photo_default);
        squareImageView.setClickable(mClickToUpload);
        int imageViewWidth = getImageWidth();
        squareImageView.setWidthByParent(imageViewWidth);

        FrameLayout frame = new FrameLayout(getContext());
        LayoutParams frameParams = new LayoutParams(imageViewWidth, imageViewWidth);
        frame.addView(squareImageView, frameParams);
        LayoutParams layoutParams = new LayoutParams(imageViewWidth, imageViewWidth);
        if (mPhotoViewIDs.size() % column != 0) layoutParams.leftMargin = childMargin;
        if(mPhotoViewIDs.size() > 0 && mPhotoViewIDs.size() % column == 0){
            mLayoutItem  = new LinearLayout(getContext());
            LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lParams.topMargin = childMargin;
            mLayoutItem.setLayoutParams(lParams);
            addView(mLayoutItem);
        }
        mLayoutItem.addView(frame, layoutParams);
        squareImageView.setId(createIndex());
        final int squarePhotoViewId = squareImageView.getId();
        mPhotoViewIDs.add(squarePhotoViewId);
        squareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickToUpload) {
                    if (((SquareImageView) v).getImageLocalUrl() == null
                        && ((SquareImageView) v).getInternetUrl() == null) {
                        doPhotoClickSelectable(squarePhotoViewId);
                    } else if (((SquareImageView) v).getImageLocalUrl() == null) {
                        //Navigator.startScaleImageSwitcher(getContext(), getInternetUrls(), position);
                        //
                    } else if (((SquareImageView) v).getInternetUrl() == null) {
                        //
                    }
                } else {
                    doScaleImage(squarePhotoViewId);
                }
            }
        });
        squareImageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (((SquareImageView) v).getImageLocalUrl() == null
                    && ((SquareImageView) v).getInternetUrl() == null) {
                    return true;
                }
                doDeletePhoto(v);
                return true;
            }
        });
    }

    private int getImageWidth() {
        return (getMeasuredWidth() - childMargin * (column - 1)) / column;
    }

    private void doDeletePhoto(View v) {
        final SquareImageView squarView = (SquareImageView) v;
        FrameLayout frame = (FrameLayout) squarView.getParent();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.RIGHT| Gravity.TOP);
        ImageView image = new ImageView(getContext());
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doPhotoDelete(squarView.getId());
            }
        });
        image.setImageResource(R.drawable.ic_delete);
        frame.addView(image, layoutParams);
    }

    private void doPhotoClickSelectable(final int photoId) {
        doUpLoadPhotoClick(photoId);
    }

    private void doPhotoDelete(final int photoId) {
        ((ViewGroup)getChildAt(0)).removeViewAt(mPhotoViewIDs.indexOf(new Integer(photoId)));
        mPhotoViewIDs.remove(new Integer(photoId));
        requestLayout();
        refresh();
    }

    private void refresh() {
        ArrayList<SquarePhotos> photos = new ArrayList<>();
        for (Integer i : mPhotoViewIDs) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(i);
            if (TextUtils.isEmpty(squareImageView.getInternetUrl()) && !TextUtils.isEmpty(squareImageView.getLocalUrl())) {
                photos.add(new SquarePhotos(squareImageView.getLocalUrl(), SquarePhotos.PhotoType.LOCAL));
            } else if (!TextUtils.isEmpty(squareImageView.getInternetUrl()) && TextUtils.isEmpty(squareImageView.getLocalUrl())) {
                photos.add(new SquarePhotos(squareImageView.getInternetUrl(), SquarePhotos.PhotoType.INTER));
            }
        }
        refreshLayout(photos);
    }

    private void refreshLayout(ArrayList<SquarePhotos> photos) {
        mPhotoViewIDs.clear();
        removeAllViews();
        initLayoutItem();
        for (int i = 0; i < photos.size(); i++) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(mPhotoViewIDs.get(mPhotoViewIDs.size() - 1));
            switch (photos.get(i).type){
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
        mListener.photoClick(viewId);
        new GetPhotoDialogFragment.Builder(mManager)
                .setPositiveListener(new GetPhotoDialogFragment.ClickListener() {
                    @Override
                    public void click() {
                        String SDState = Environment.getExternalStorageState();
                        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
                            ActivityCompat.startActivityForResult((Activity) getContext(),
                                    new Intent(getContext(), UseCameraActivity.class), NavigatorImage.RESULT_TAKE_PHOTO,
                                    null);
                            //NavUtils.startUseCameraActivity(getActivity());
                        } else {
                            Toast.makeText(getContext(), "内存卡不存在", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeListener(new GetPhotoDialogFragment.ClickListener() {
                    @Override
                    public void click() {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity) getContext()).startActivityForResult(i, NavigatorImage.RESULT_SELECT_PHOTO);
                    }
                })
                .show();
    }

    private void doScaleImage(int viewId) {
        //TODO scale
    }

    public void addPhoto(int viewId) {
        boolean isEndPhotoView = true;
        for (int i = 0; i < mPhotoViewIDs.size() - 1; i++) {
            if (mPhotoViewIDs.get(i) == viewId) {
                isEndPhotoView = false;
            }
        }
        if (isEndPhotoView && (maxImageNum == -1 || mPhotoViewIDs.size() < maxImageNum)) {
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

    public HashMap<String, String> getUploadImageMap() {
        HashMap<String, String> map = new HashMap<>();
        for (Integer i : mPhotoViewIDs) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(i);
            if (TextUtils.isEmpty(squareImageView.getInternetUrl())) {
                map.put(squareImageView.getUploadImageKey(), squareImageView.getImageLocalUrl());
            }
        }
        return map;
    }

    public ArrayList<String> getLocalUrls() {
        ArrayList<String> map = new ArrayList<>();
        for (Integer i : mPhotoViewIDs) {
            SquareImageView squareImageView = (SquareImageView) this.findViewById(i);
            if (TextUtils.isEmpty(squareImageView.getInternetUrl())  && !TextUtils.isEmpty(squareImageView.getLocalUrl())) {
                map.add(squareImageView.getLocalUrl());
            }
        }
        return map;
    }

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

    public SquareImageView findFirstSquareView() {
        return (SquareImageView) this.findViewById(mPhotoViewIDs.get(0));
    }

    public void setGroupItemClick(PhotoGroupViewClickListener listener) {
        mListener = listener;
    }

    public void setFragmentManager(FragmentManager manager) {
        mManager = manager;
    }

    public void setClickAble(boolean able) {
        mClickToUpload = able;
    }

    private int createIndex() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("mmss");
        String date = sdf.format(new Date(time));
        int num1 = Integer.valueOf(date + Long.toString(time % 1000));
        int i = num1 * 10 + mPhotoViewIDs.size();
        return i;
    }

}
