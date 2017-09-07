package com.loopeer.android.apps.imagegroupview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.loopeer.android.librarys.imagegroupview.OnImageClickListener;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initGridView();
    }


    private void initGridView() {
        mGridView = (ImageGridView) findViewById(R.id.grid_test_group);
        mGridView2 = (ImageGridView) findViewById(R.id.grid_test_group2);
        mSingleImageView = (SingleImageView) findViewById(R.id.avatar);
        mGridView.updateNetPhotos(createTestData());
    }

    private ArrayList<String> createTestData() {
        ArrayList<String> result = new ArrayList<>();
        result.add("http://img2.imgtn.bdimg.com/it/u=2042486160,1330339958&fm=200&gp=0.jpg");
        result.add("http://img1.3lian.com/img13/c3/82/d/64.jpg");
        result.add("http://img3.duitang.com/uploads/item/201601/23/20160123202447_kYyjz.jpeg");
        result.add("http://img.ycwb.com/news/attachement/jpg/site2/20130328/90fba601558912bdd7bd4d.jpg");
        result.add("http://h.hiphotos.baidu.com/zhidao/pic/item/5fdf8db1cb1349549e1cd9df534e9258d1094a0e.jpg");
        result.add("http://duwanjuancn.qiniudn.com/wp-content/uploads/20141018/20141018185310259.jpg");
        result.add("http://img.7160.com/uploads/allimg/160513/12-160513155949.jpg");
        result.add("http://img2.imgtn.bdimg.com/it/u=1724555645,204198543&fm=27&gp=0.jpg");
        return result;
    }

    @Override
    public void onImageClick(View clickImage, SquareImage squareImage) {

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
}
