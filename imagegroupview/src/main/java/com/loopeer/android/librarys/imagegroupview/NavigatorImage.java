package com.loopeer.android.librarys.imagegroupview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;
import com.loopeer.android.librarys.imagegroupview.activity.ImageSwitcherActivity;
import com.loopeer.android.librarys.imagegroupview.model.SquareImage;
import com.loopeer.android.librarys.imagegroupview.view.ImageGridView;
import com.loopeer.android.librarys.imagegroupview.view.SingleImageView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NavigatorImage {

    public static final String[] PERMISSION_CAMERA_STARTREQUEST = new String[]{Manifest.permission.CAMERA};
    public static final String[] PERMISSION_WRITE_STARTREQUEST = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUEST_CAMERA_STARTREQUEST = 1;
    public static final int REQUEST_WRITE_STARTREQUEST = 2;

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
    public static final String EXTRA_DRAG_DISMISS = "extra_drag_dismiss";
    public static final String EXTRA_IS_AVATAR_CROP = "extra_is_avatar_crop";


    public static final int RESULT_SELECT_PHOTO = 2001;
    public static final int RESULT_TAKE_PHOTO = 2003;
    public static final int RESULT_IMAGE_SWITCHER = 2004;
    public static final int RESULT_SELECT_PHOTOS = 2005;

    private static final String CROPPED_IMAGE_NAME = "image_group_view_cropped_image.jpg";
    private static int ASPECT_RATIO_X = 1;
    private static int ASPECT_RATIO_Y = 1;

    public static final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    public static void startImageSwitcherActivity(Context context, List<SquareImage> images,
                                                  int position, boolean showAddButton,
                                                  int placeholderDrawable, int groupId,
                                                  boolean dragDismiss) {
        Intent intent;
        intent = new Intent(getImageGroupIntentAction(context));
        intent.setData(Uri.parse(getImageGroupIntentSwitcherUri(context)));
        if (intent == null || intent.resolveActivity(context.getPackageManager()) == null) {
            intent = new Intent(context, ImageSwitcherActivity.class);
        }
        addImageSwitcherData(images, position, showAddButton, placeholderDrawable, groupId, intent, dragDismiss);
        ((Activity) context).startActivityForResult(intent, RESULT_IMAGE_SWITCHER);
    }

    private static void addImageSwitcherData(List<SquareImage> images, int position,
                                             boolean showAddButton, int placeholderDrawable,
                                             int groupId, Intent intent, boolean dragDismiss) {
        intent.putParcelableArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL, new ArrayList<Parcelable>(images));
        intent.putExtra(EXTRA_IMAGE_DELETE, showAddButton);
        intent.putExtra(EXTRA_IMAGE_URL_POSITION, position);
        intent.putExtra(EXTRA_IMAGE_PLACE_DRAWABLE_ID, placeholderDrawable);
        intent.putExtra(EXTRA_IMAGE_GROUP_ID, groupId);
        intent.putExtra(EXTRA_DRAG_DISMISS, dragDismiss);
    }

    public static void startCustomAlbumActivity(Context context, int canSelectMaxNum, int groupId) {
        Intent intent;
        intent = new Intent(getImageGroupIntentAction(context));
        intent.setData(Uri.parse(getImageGroupIntentAlbumUri(context)));
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
        intent = new Intent(getImageGroupIntentAction(context));
        intent.setData(Uri.parse(getImageGroupIntentAlbumUri(context)));
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

    public static void startAvatarAlbumActivity(Context context, int groupId, int type) {
        startAvatarAlbumActivity(context, groupId, type, 1, 1);
    }

    public static void startAvatarAlbumActivity(Context context, int groupId, int type, int aspectRatioX, int aspectRatioY) {
        ASPECT_RATIO_X = aspectRatioX;
        ASPECT_RATIO_Y = aspectRatioY;
        Intent intent;
        intent = new Intent(getImageGroupIntentAction(context));
        intent.setData(Uri.parse(getImageGroupIntentAlbumUri(context)));
        if (intent == null || intent.resolveActivity(context.getPackageManager()) == null) {
            intent = new Intent(context, AlbumActivity.class);
        }
        intent.putExtra(EXTRA_IS_AVATAR_CROP, true);
        addAlbumDataWithType(1, groupId, type, intent, EXTRA_IMAGE_SELECT_MAX_NUM, EXTRA_IMAGE_GROUP_ID, EXTRA_ALBUM_TYPE);
        ((Activity) context).startActivityForResult(intent, RESULT_SELECT_PHOTOS);
    }

    public static void startCropActivity(Context context, String uri, boolean isAvatar,
                                         @ColorRes int toolbarColor,@ColorRes int statusBarColor) {
        UCrop uCrop = UCrop.of(Uri.parse(uri), Uri.fromFile(new File(context.getCacheDir(), CROPPED_IMAGE_NAME)));

        if (isAvatar) {
            uCrop.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(ContextCompat.getColor(context, toolbarColor));
            options.setStatusBarColor(ContextCompat.getColor(context, statusBarColor));
            options.setToolbarTitle("  ");
            options.setHideBottomControls(true);

            uCrop.withOptions(options);
        }

        uCrop.start((Activity) context);
    }

    private static String getImageGroupIntentAction(Context context) {
        return context.getString(R.string.image_group_intent_action);
    }

    private static String getImageGroupIntentSwitcherUri(Context context) {
        return context.getString(R.string.image_group_intent_switcher_uri);
    }

    private static String getImageGroupIntentAlbumUri(Context context) {
        return context.getString(R.string.image_group_intent_album_uri);
    }
}