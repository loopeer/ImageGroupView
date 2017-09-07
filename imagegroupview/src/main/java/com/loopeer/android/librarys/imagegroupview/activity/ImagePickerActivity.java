package com.loopeer.android.librarys.imagegroupview.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.view.ImageGridView;

import java.util.ArrayList;

//TODO step1. 长点击事件滑动图片；
//TODO step2. 对应图片消失留白；
//TODO step3. 图片划经位置其他图片滚动动画；
//TODO 三个页面逻辑和启动模式
public class ImagePickerActivity extends AppCompatActivity {

    private ImageGridView mGridView;
    private ArrayList<String> urls = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        mGridView = (ImageGridView) findViewById(R.id.grid_test_group);
        pareIntent();
    }

    private void pareIntent() {
        Intent intent = this.getIntent();

        mGridView.onParentResults(NavigatorImage.RESULT_SELECT_PHOTOS,intent);

    }


}
