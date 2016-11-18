package com.loopeer.android.librarys.imagegroupview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.AlbumRecyclerAdapter;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;
import com.loopeer.android.librarys.imagegroupview.utils.DisplayUtils;

import java.util.List;

public class CustomPopupView extends LinearLayout implements View.OnClickListener, AlbumRecyclerAdapter.OnItemClickListener {

    private View mBgView;
    private RecyclerView mRecyclerView;
    private TextView mTextImagesNum;
    private AlbumRecyclerAdapter mFolderRecyclerAdaper;
    private boolean mIsShowing;
    private FolderItemSelectListener mFolderItemSelectListener;

    public CustomPopupView(Context context) {
        this(context, null);
    }

    public CustomPopupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_popup_folder, this);
        mBgView = findViewById(R.id.view_album_popup_bg);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_album_popup);
        mTextImagesNum = (TextView) findViewById(R.id.text_images_num);

        mFolderRecyclerAdaper = new AlbumRecyclerAdapter(getContext());
        mFolderRecyclerAdaper.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mFolderRecyclerAdaper);
        mTextImagesNum.setOnClickListener(this);
        mBgView.setOnClickListener(this);
    }

    private void show() {
        mIsShowing = true;
        startShowWindowAnimation();
    }

    public void dismiss() {
        mIsShowing = false;
        startDismissWindowAnimation();
    }

    private boolean isShowing() {
        return mIsShowing;
    }

    private void togglePopupWindow() {
        if (isShowing()) {
            dismiss();
        } else {
            show();
        }
    }

    private void startShowWindowAnimation() {
        startShowAnimation(0, calculateWindowHeight(), true);
    }

    private void startDismissWindowAnimation() {
        startShowAnimation(calculateWindowHeight(), 0, false);
    }

    private void startShowAnimation(final int startHeight, final int endHeight, final boolean isShow) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                if (height == startHeight && isShow) {
                    mRecyclerView.setVisibility(VISIBLE);
                    mBgView.setVisibility(VISIBLE);
                }
                setRecyclerViewHeight(height);
                setBgViewAlpha((float) height / (float) Math.abs(endHeight - startHeight));
                if (height == endHeight && !isShow) {
                    mRecyclerView.setVisibility(GONE);
                    mBgView.setVisibility(GONE);
                }
            }
        });
        valueAnimator.start();
    }

    private void setBgViewAlpha(float v) {
        mBgView.setAlpha(v);
    }

    private int getFooterHeight() {
        return mTextImagesNum.getHeight();
    }

    private void setRecyclerViewHeight(int height) {
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        params.height = height;
        mRecyclerView.setLayoutParams(params);
    }

    private int calculateWindowHeight() {
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.image_select_folder_height);
        int actualHeight = mFolderRecyclerAdaper.getItemCount() * itemHeight;
        int maxPopupHeight = DisplayUtils.getScreenHeight(getContext()) * 5 / 8;
        return Math.min(maxPopupHeight, actualHeight);
    }

    public void setNumText(String str) {
        mTextImagesNum.setText(str);
    }

    public void updateFolderData(List list) {
        mFolderRecyclerAdaper.updateData(list);

        updateDefaultImages();
    }

    public void setFolderItemSelectListener(FolderItemSelectListener listener) {
        mFolderItemSelectListener = listener;
    }

    private void updateDefaultImages() {
        onItemSelect(mFolderRecyclerAdaper.getItem(0));
    }

    private void onItemSelect(ImageFolder item) {
        mTextImagesNum.setText(item.name);
        dismiss();
        mFolderItemSelectListener.onFolderItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_images_num) {
            togglePopupWindow();
        } else if (v.getId() == R.id.view_album_popup_bg) {
            dismiss();
        }

    }

    @Override
    public void onItemClick(ImageFolder imageFolder) {
        onItemSelect(imageFolder);
    }

    public interface FolderItemSelectListener {
        void onFolderItemSelected(ImageFolder imageFolder);
    }
}
