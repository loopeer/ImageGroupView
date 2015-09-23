package com.loopeer.android.apps.imagegroupview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.ImageGroupDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.ImageGroupView;
import com.loopeer.android.librarys.imagegroupview.SquareImage;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ImageGroupView.OnImageClickListener {

    private ImageGroupView imageGroupAddAble;
    private ImageGroupView imageGroup;
    private ImageGroupView imageGroup2;
    private SimpleDraweeView oneImage;
    private SimpleDraweeView oneImage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();
    }

    private void initView() {
        imageGroup = (ImageGroupView) findViewById(R.id.images_group);
        imageGroup2 = (ImageGroupView) findViewById(R.id.images_group_2);
        imageGroupAddAble = (ImageGroupView) findViewById(R.id.images_group_addable);
        oneImage = (SimpleDraweeView) findViewById(R.id.image_one_show);
        oneImage2 = (SimpleDraweeView) findViewById(R.id.image_one_show2);
    }

    private void setData() {
        imageGroupAddAble.setFragmentManager(getSupportFragmentManager());
        imageGroupAddAble.setPhotos(createTestData());
        imageGroup.setPhotos(createTestData());
        imageGroup2.setPhotos(createTestData());
        imageGroup.setOnImageClickListener(this);

        ImageGroupDisplayHelper.displayImageOneShow(oneImage, "http://img1.3lian.com/img13/c3/82/d/64.jpg", getResources().getDimensionPixelSize(R.dimen.image_dimen));
        ImageGroupDisplayHelper.displayImageOneShow(oneImage2, "http://pic2.ooopic.com/10/23/79/75bOOOPICa3.jpg", getResources().getDimensionPixelSize(R.dimen.image_dimen));
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
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imageGroupAddAble.onParentResult(requestCode, data);
        }
    }

    @Override
    public void onImageClick(SquareImage clickImage, ArrayList<SquareImage> squareImages, ArrayList<String> allImageInternetUrl) {
        Toast.makeText(this, "Inter Images is :  " + allImageInternetUrl, Toast.LENGTH_SHORT).show();
    }
}
