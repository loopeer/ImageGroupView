package com.loopeer.android.librarys.imagegroupview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ViewAnimator;

import com.loopeer.android.librarys.imagegroupview.DividerItemImagesDecoration;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.adapter.ImageAdapter;
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder;

import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private RecyclerView mReyclerView;
    private ViewAnimator mViewAnimator;
    private ImageAdapter mImageAdapter;

    private ImageFolder mImageFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mReyclerView = (RecyclerView) findViewById(R.id.recycler_album);
        mViewAnimator = (ViewAnimator) findViewById(R.id.view_album_animator);

        parseIntent();
        showProgressView();
        setUpRecyclerView();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        mImageFolder = (ImageFolder) intent.getSerializableExtra(NavigatorImage.EXTRA_IMAGE_FOLDER);
    }

    private void updateContentView(List floders) {
        if (floders.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }

    private void showContentView() {
        mViewAnimator.setDisplayedChild(2);
    }

    private void showEmptyView() {
        mViewAnimator.setDisplayedChild(1);
    }

    private void showProgressView() {
        mViewAnimator.setDisplayedChild(0);
    }

    private void setUpRecyclerView() {

        mReyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mReyclerView.addItemDecoration(
                new DividerItemImagesDecoration(
                        getResources().getDimensionPixelSize(R.dimen.inline_padding)));
        mReyclerView.setPadding(
                getResources().getDimensionPixelSize(R.dimen.inline_padding) / 2,
                0,
                getResources().getDimensionPixelSize(R.dimen.inline_padding) / 2,
                0
        );
        mImageAdapter = new ImageAdapter(this);
        mReyclerView.setAdapter(mImageAdapter);
    }

}
