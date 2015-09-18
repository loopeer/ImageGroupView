package com.loopeer.android.apps.imagegroupview;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.loopeer.android.librarys.imagegroupview.ImageGroupView;
import com.loopeer.android.librarys.imagegroupview.NavigatorImage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageGroupView imageGroupAddAble;
    private ImageGroupView imageGroup;

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
    }

    private void setData() {
        imageGroupAddAble.setFragmentManager(getSupportFragmentManager());
        imageGroup.setPhotos(createTestData());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String photoTakeurl = data.getStringExtra(NavigatorImage.EXTRA_PHOTO_URL);
            Uri imageSelectedUri = data.getData();
            imageGroupAddAble.onParentResult(requestCode, photoTakeurl, imageSelectedUri);
        }
    }
}
