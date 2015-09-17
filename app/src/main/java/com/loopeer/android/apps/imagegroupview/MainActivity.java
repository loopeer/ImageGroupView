package com.loopeer.android.apps.imagegroupview;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.ImageGroupView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ImageGroupView.PhotoGroupViewClickListener {

    private ImageGroupView imageGroupAddAble;
    private ImageGroupView imageGroup;
    private SimpleDraweeView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();
    }

    private void initView() {
        imageGroup = (ImageGroupView) findViewById(R.id.images_group);
        imageGroupAddAble = (ImageGroupView) findViewById(R.id.images_group_addable);
        image = (SimpleDraweeView) findViewById(R.id.image_example);
    }

    private void setData() {
        imageGroupAddAble.setFragmentManager(getSupportFragmentManager());
        imageGroupAddAble.setGroupItemClick(this);/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageGroup.setPhotos(createTestData());
            }
        }, 500);*/
        imageGroup.setPhotos(createTestData());
        image.setImageURI(Uri.parse("http://img3.imgtn.bdimg.com/it/u=4219766182,574313781&fm=21&gp=0.jpg"));
    }

    private ArrayList<String> createTestData() {
        ArrayList<String> result = new ArrayList<>();
        result.add("http://img4.imgtn.bdimg.com/it/u=434281914,1810736344&fm=21&gp=0.jpg");
        result.add("http://img1.3lian.com/img13/c3/82/d/64.jpg");
        result.add("http://img3.imgtn.bdimg.com/it/u=4219766182,574313781&fm=21&gp=0.jpg");
        result.add("http://img0.imgtn.bdimg.com/it/u=3326236795,89092727&fm=21&gp=0.jpg");
        result.add("http://img5.imgtn.bdimg.com/it/u=2645226784,1850525231&fm=21&gp=0.jpg");
        result.add("http://img5.imgtn.bdimg.com/it/u=1849768669,2102852722&fm=21&gp=0.jpg");
        result.add("http://img5.imgtn.bdimg.com/it/u=138250353,211064234&fm=21&gp=0.jpg");
        result.add("http://img4.imgtn.bdimg.com/it/u=434281914,1810736344&fm=21&gp=0.jpg");
        result.add("http://img3.imgtn.bdimg.com/it/u=4024518858,124891761&fm=21&gp=0.jpg");
        return result;
    }

    @Override
    public void photoClick(int clickingViewId) {

    }
}
