package com.loopeer.android.librarys.imagegroupview.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.model.Image;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.view.DragRecycleAdapter;
import com.loopeer.android.librarys.imagegroupview.view.DragRecycleView;
import com.loopeer.android.librarys.imagegroupview.view.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class ImagePickerActivity extends AppCompatActivity implements View.OnClickListener {


    List<Image> images;
    List<SquareImage> preImages;
    public TextView tv;
    private MenuItem mSubmitMenu;
    private TextView mTextSubmit;
    private int mImageGroupId;
    DragRecycleAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        pareIntent();
        initData();
        initView();
    }

    public void initData() {
        preImages = new ArrayList<>();
    }

    private void initView() {
        DragRecycleView dragRecycleView = (DragRecycleView) findViewById(R.id.drag_view);
        tv = (TextView) findViewById(R.id.delete);
        dragRecycleView.setLayoutManager(new GridLayoutManager(ImagePickerActivity.this, 4));
        dragRecycleView.setHasFixedSize(true);
        dragRecycleView.tv = tv;

        for (Image image : images) {
            SquareImage squareImage = new SquareImage(image.url, null, getPhotoKey(image.time), SquareImage.PhotoType.LOCAL);
            preImages.add(squareImage);
        }
        adapter = new DragRecycleAdapter(this, preImages);
        dragRecycleView.setAdapter(adapter);
        //创建SimpleItemTouchHelperCallback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(dragRecycleView);
    }

    private void pareIntent() {
        Intent intent = this.getIntent();
        images = (List<Image>) intent.getSerializableExtra(NavigatorImage.EXTRA_PHOTOS_URL);
        mImageGroupId = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, 0);
    }

    @NonNull
    private String getPhotoKey(long time) {
        return "image_" + time;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_group_submit, menu);
        updateSubmitMenu(menu);
        return true;
    }

    private void updateSubmitMenu(Menu menuItem) {
        mSubmitMenu = menuItem.findItem(R.id.action_submit);
        View view = mSubmitMenu.getActionView();
        mTextSubmit = (TextView) view.findViewById(R.id.text_image_submit);
        mTextSubmit.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Log.d("activityLog", "submit");
        finishWithResult();
    }

    private void finishWithResult() {
        ArrayList<String> pics = new ArrayList<>();
// = adapter.mData.;
        for (SquareImage s : adapter.mData
                ) {
            Log.d("log ", "mdata  " + s.localUrl);
            pics.add(s.localUrl);
        }
        Intent intent = getIntent();
        intent.putExtra(NavigatorImage.EXTRA_PHOTOS_URL, pics);
        intent.putExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, mImageGroupId);
        setResult(RESULT_OK, intent);
        finish();
    }
}
