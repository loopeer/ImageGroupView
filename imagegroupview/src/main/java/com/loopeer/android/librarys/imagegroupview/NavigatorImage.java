package com.loopeer.android.librarys.imagegroupview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;
import com.loopeer.android.librarys.imagegroupview.activity.ImageSwitcherActivity;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

import java.util.ArrayList;
import java.util.List;

public class NavigatorImage {

    public static final String[] PERMISSION_CAMERA_STARTREQUEST = new String[]{Manifest.permission.CAMERA};
    public static final String[] PERMISSION_WRITE_STARTREQUEST = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_CAMERA_STARTREQUEST = 1;
    public static final int REQUEST_WRITE_STARTREQUEST = 2;

    public static final String ACTION_IMAGE_GROUP = "com.loopeer.android.librarys.imagegroupview";
    public static final String URI_IMAGEGROUPVIEW_SWITCHER = "imagegroupview://switcher";
    public static final String URI_IMAGEGROUPVIEW_ALBUM = "imagegroupview://album";

    public static final String EXTRA_PHOTO_URL = "extra_photo_url";
    public static final String EXTRA_PHOTOS_URL = "extra_photos_url";
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_IMAGE_SELECT_MAX_NUM = "extra_image_select_max_num";
    public static final String EXTRA_IMAGE_GROUP_ID = "extra_image_group_id";
    public static final String EXTRA_IMAGE_URL_POSITION = "image_position";
    public static final String EXTRA_IMAGE_DELETE = "extra_image_delete";
    public static final String EXTRA_IMAGE_PLACE_DRAWABLE_ID = "extra_image_place_drawable_id";
    public static final String EXTRA_IMAGE_FOLDER = "extra_image_folder";
    public static final String EXTRA_ALBUM_TYPE = "extra_album_type";


    public static final int RESULT_SELECT_PHOTO = 2001;
    public static final int RESULT_TAKE_PHOTO = 2003;
    public static final int RESULT_IMAGE_SWITCHER = 2004;
    public static final int RESULT_SELECT_PHOTOS = 2005;

    public static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    public static void startImageSwitcherActivity(Context context, List<SquareImage> images, int position, boolean showAddButton, int placeholderDrawable, int groupId) {
        Intent intent;
        intent = new Intent(ACTION_IMAGE_GROUP);
        intent.setData(Uri.parse(URI_IMAGEGROUPVIEW_SWITCHER));
        if (intent == null || intent.resolveActivity(context.getPackageManager()) == null) {
            intent = new Intent(context, ImageSwitcherActivity.class);
        }
        addImageSwitcherData(images, position, showAddButton, placeholderDrawable, groupId, intent);
        ((Activity) context).startActivityForResult(intent, RESULT_IMAGE_SWITCHER);
    }

    private static void addImageSwitcherData(List<SquareImage> images, int position, boolean showAddButton, int placeholderDrawable, int groupId, Intent intent) {
        intent.putParcelableArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL, new ArrayList<Parcelable>(images));
        intent.putExtra(EXTRA_IMAGE_DELETE, showAddButton);
        intent.putExtra(EXTRA_IMAGE_URL_POSITION, position);
        intent.putExtra(EXTRA_IMAGE_PLACE_DRAWABLE_ID, placeholderDrawable);
        intent.putExtra(EXTRA_IMAGE_GROUP_ID, groupId);
    }

    public static void startCustomAlbumActivity(Context context, int canSelectMaxNum, int groupId) {
        Intent intent;
        intent = new Intent(ACTION_IMAGE_GROUP);
        intent.setData(Uri.parse(URI_IMAGEGROUPVIEW_ALBUM));
        if (intent == null || intent.resolveActivity(context.getPackageManager()) == null) {
            intent = new Intent(context, AlbumActivity.class);
        }
        addAlbumData(canSelectMaxNum, groupId, intent, EXTRA_IMAGE_SELECT_MAX_NUM, EXTRA_IMAGE_GROUP_ID);
        ((Activity) context).startActivityForResult(intent, RESULT_SELECT_PHOTOS);
    }

    private static void addAlbumData(int canSelectMaxNum, int groupId, Intent intent, String extraImageSelectMaxNum, String extraImageGroupId) {
        intent.putExtra(extraImageSelectMaxNum, canSelectMaxNum);
        intent.putExtra(extraImageGroupId, groupId);
    }

    public static void startCustomAlbumActivity(Context context, int canSelectMaxNum, int groupId, int type) {
        Intent intent;
        intent = new Intent(ACTION_IMAGE_GROUP);
        intent.setData(Uri.parse(URI_IMAGEGROUPVIEW_ALBUM));
        if (intent == null || intent.resolveActivity(context.getPackageManager()) == null) {
            intent = new Intent(context, AlbumActivity.class);
        }
        addAlbumDataWithType(canSelectMaxNum, groupId, type, intent, EXTRA_IMAGE_SELECT_MAX_NUM, EXTRA_IMAGE_GROUP_ID, EXTRA_ALBUM_TYPE);
        ((Activity) context).startActivityForResult(intent, RESULT_SELECT_PHOTOS);
    }

    private static void addAlbumDataWithType(int canSelectMaxNum, int groupId, int type, Intent intent, String extraImageSelectMaxNum, String extraImageGroupId, String extraAlbumType) {
        intent.putExtra(extraImageSelectMaxNum, canSelectMaxNum);
        intent.putExtra(extraImageGroupId, groupId);
        intent.putExtra(extraAlbumType, type);
    }

}