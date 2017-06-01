package com.loopeer.android.apps.imagegroupview;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.loopeer.android.librarys.imagegroupview.OnImageClickListener;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.Album;
import com.loopeer.android.librarys.imagegroupview.view.ImageGridView;
import com.loopeer.android.librarys.imagegroupview.view.SingleImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnImageClickListener {

    private ImageGridView mGridView;
    private ImageGridView mGridView2;
    private SingleImageView mSingleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGridView();
    }

    private void initGridView() {
        mGridView = (ImageGridView) findViewById(R.id.grid_test_group);
        mGridView2 = (ImageGridView) findViewById(R.id.grid_test_group2);
        mSingleImageView = (SingleImageView) findViewById(R.id.avatar);
        mGridView.updateNetPhotos(createTestData());

        mGridView2.setAlbumOptions(new Album.Options()
                .setStatusBarColor(Color.RED).setToolBarColor(Color.BLACK)
                .setToolBarTitle("kshfd").setSubmitButtonTextPrefix("fsdfsdfsdfs"));
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
            mGridView.onParentResult(requestCode, data);
            mGridView2.onParentResult(requestCode, data);
            mSingleImageView.onParentResult(requestCode, data);
        }
    }

    @Override
    public void onImageClick(View clickImage, SquareImage squareImage) {

    }
}
