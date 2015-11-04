package com.loopeer.android.librarys.imagegroupview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ImagesSwitcherAdapter extends FragmentPagerAdapter {

    private ArrayList<SquareImage> images;
    private OnTabOneClickListener onTabOneClickListener;

    public ImagesSwitcherAdapter(FragmentManager fm) {
        super(fm);
        images = new ArrayList<>();
    }

    public void setOnTabOneClickListener(OnTabOneClickListener onTabOneClickListener) {
        this.onTabOneClickListener = onTabOneClickListener;
    }

    public void setImages(ArrayList<SquareImage> strings) {
        images.clear();
        images.addAll(strings);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        ScaleImageFragment scaleImageFragment = ScaleImageFragment.newInstance(images.get(position));
        scaleImageFragment.setOneTabListener(onTabOneClickListener);
        return scaleImageFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ScaleImageFragment scaleImageFragment = (ScaleImageFragment) super.instantiateItem(container, position);
        scaleImageFragment.setSquareImage(images.get(position));
        return scaleImageFragment;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
