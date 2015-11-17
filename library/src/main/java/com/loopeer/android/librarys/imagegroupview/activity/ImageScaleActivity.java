package com.loopeer.android.librarys.imagegroupview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.loopeer.android.librarys.imagegroupview.NavigatorImage;
import com.loopeer.android.librarys.imagegroupview.OnTabOneClickListener;
import com.loopeer.android.librarys.imagegroupview.R;
import com.loopeer.android.librarys.imagegroupview.ScaleImageFragment;
import com.loopeer.android.librarys.imagegroupview.SquareImage;

public class ImageScaleActivity extends AppCompatActivity implements OnTabOneClickListener {

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_image);

        pareIntent();
        setFragment();
    }

    private void pareIntent() {
        Intent intent = getIntent();
        url = intent.getStringExtra(NavigatorImage.EXTRA_IMAGE_URL);
    }

    private void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ScaleImageFragment scaleImageFragment = ScaleImageFragment.newInstance(new SquareImage(null, url, null, SquareImage.PhotoType.INTER));
        scaleImageFragment.setOneTabListener(this);
        fragmentTransaction.add(R.id.container, scaleImageFragment).commit();
    }

    @Override
    public void onTabOneClick() {
        this.finish();
    }
}
