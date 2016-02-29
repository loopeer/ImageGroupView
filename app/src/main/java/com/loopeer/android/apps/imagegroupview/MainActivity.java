package com.loopeer.android.apps.imagegroupview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.android.librarys.imagegroupview.ImageGroupView;
import com.loopeer.android.librarys.imagegroupview.OnImageClickListener;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.utils.ImageGroupDisplayHelper;
import com.loopeer.android.librarys.imagegroupview.view.ImageGridView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements OnImageClickListener {

    private ImageGroupView imageGroupAddAble;
    private ImageGroupView imageGroup;
    private ImageGroupView imageGroup2;
    private ImageGridView mGridView;
    private ImageGridView mGridView2;
    private SimpleDraweeView oneImage;
    private SimpleDraweeView oneImage2;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();
        initGridView();
    }

    private void initGridView() {
        mGridView = (ImageGridView) findViewById(R.id.grid_test_group);
        mGridView2 = (ImageGridView) findViewById(R.id.grid_test_group2);
        mGridView.updateNetPhotos(createTestData());
    }

    private void initView() {
        imageGroup = (ImageGroupView) findViewById(R.id.images_group);
        imageGroup2 = (ImageGroupView) findViewById(R.id.images_group_2);
        imageGroupAddAble = (ImageGroupView) findViewById(R.id.images_group_addable);
        oneImage = (SimpleDraweeView) findViewById(R.id.image_one_show);
        oneImage2 = (SimpleDraweeView) findViewById(R.id.image_one_show2);
        text = (TextView) findViewById(R.id.text);
    }

    private void setData() {
        imageGroupAddAble.setFragmentManager(getSupportFragmentManager());
        imageGroupAddAble.setNetworkPhotos(createTestData());
        imageGroup.setNetworkPhotos(createTestData());
        imageGroup2.setNetworkPhotos(createTestData());
        imageGroup2.setLocalPhotos(Arrays.asList(new String[]{"/storage/emulated/0/Pictures/1447745372984.jpg"}));
        imageGroup.setOnImageClickListener(this);

        ImageGroupDisplayHelper.displayImageOneShow(oneImage, "http://img1.3lian.com/img13/c3/82/d/64.jpg", getResources().getDimensionPixelSize(R.dimen.image_dimen));
        ImageGroupDisplayHelper.displayImageOneShow(oneImage2, "http://pic2.ooopic.com/10/23/79/75bOOOPICa3.jpg", getResources().getDimensionPixelSize(R.dimen.image_dimen));
        text.setText(Html.fromHtml("<strike>从首批发布的赞助商来看，全球云计算大会·中国站“本土化”成果显著，国内厂商的大力投入充分显示了对该平台的认可，也积极响应了国务院“积极开展国际合作、整合国际创新资源、加强国内外企业研发合作”的号召。战略合作伙伴、首席赞助商等独家权益早早被预订，一些新兴的云计算品牌也把大会作为商务和宣传重地，积极参与各项展示与合作。</strike>"));
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
        }
    }

    @Override
    public void onImageClick(SquareImage clickImage, ArrayList<SquareImage> squareImages, ArrayList<String> allImageInternetUrl) {
        Toast.makeText(this, "Inter Images is :  " + allImageInternetUrl, Toast.LENGTH_SHORT).show();
    }
}
