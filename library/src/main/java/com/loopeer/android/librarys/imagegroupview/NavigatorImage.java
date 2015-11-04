package com.loopeer.android.librarys.imagegroupview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class NavigatorImage {

    public static final String EXTRA_PHOTO_URL = "extra_photo_url";
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_IMAGE_URL_POSITION = "image_position";
    public static final String EXTRA_IMAGE_DELETE = "extra_image_delete";

    public static final int RESULT_SELECT_PHOTO = 2001;
    public static final int RESULT_TAKE_PHOTO = 2003;
    public static final int RESULT_IMAGE_SWITCHER = 2004;

    public static void startImageSwitcherActivity(Context context, ArrayList<SquareImage> images, int position, boolean showAddButton) {
        Intent intent = new Intent(context, ImageSwitcherActivity.class);
        intent.putParcelableArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL, images);
        intent.putExtra(EXTRA_IMAGE_DELETE, showAddButton);
        intent.putExtra(EXTRA_IMAGE_URL_POSITION, position);
        ((Activity)context).startActivityForResult(intent, RESULT_IMAGE_SWITCHER);
    }

}