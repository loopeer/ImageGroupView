package com.loopeer.android.librarys.imagegroupview

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity
import com.loopeer.android.librarys.imagegroupview.activity.ImageSwitcherActivity
import com.loopeer.android.librarys.imagegroupview.activity.UIPatternActivity
import com.loopeer.android.librarys.imagegroupview.model.SquareImage
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

object NavigatorImage {

    val PERMISSION_CAMERA_STARTREQUEST = arrayOf(Manifest.permission.CAMERA)
    val PERMISSION_WRITE_STARTREQUEST = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    const val REQUEST_CAMERA_STARTREQUEST = 1
    const val REQUEST_WRITE_STARTREQUEST = 2

    const val EXTRA_PHOTO_URL = "extra_photo_url"
    const val EXTRA_PHOTOS_URL = "extra_photos_url"
    const val EXTRA_IMAGE_URL = "image_url"
    const val EXTRA_IMAGE_SELECT_MAX_NUM = "extra_image_select_max_num"
    const val EXTRA_IMAGE_GROUP_ID = "extra_image_group_id"
    const val EXTRA_IMAGE_URL_POSITION = "image_position"
    const val EXTRA_IMAGE_DELETE = "extra_image_delete"
    const val EXTRA_IMAGE_PLACE_DRAWABLE_ID = "extra_image_place_drawable_id"
    const val EXTRA_IMAGE_FOLDER = "extra_image_folder"
    const val EXTRA_ALBUM_TYPE = "extra_album_type"
    const val EXTRA_DRAG_DISMISS = "extra_drag_dismiss"
    const val EXTRA_IS_AVATAR_CROP = "extra_is_avatar_crop"


    const val RESULT_SELECT_PHOTO = 2001
    const val RESULT_TAKE_PHOTO = 2003
    const val RESULT_IMAGE_SWITCHER = 2004
    const val RESULT_SELECT_PHOTOS = 2005

    private const val CROPPED_IMAGE_NAME = "image_group_view_cropped_image.jpg"
    private var ASPECT_RATIO_X = 1
    private var ASPECT_RATIO_Y = 1

    val IMAGE_PROJECTION = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media._ID)

    fun startImageSwitcherActivity(context: Context, images: List<SquareImage>,
                                   position: Int, showAddButton: Boolean,
                                   placeholderDrawable: Int, groupId: Int,
                                   dragDismiss: Boolean) {
        var intent: Intent?
        intent = Intent(getImageGroupIntentAction(context))
        intent.data = Uri.parse(getImageGroupIntentSwitcherUri(context))
        if (intent == null || intent.resolveActivity(context.packageManager) == null) {
            intent = Intent(context, ImageSwitcherActivity::class.java)
        }
        addImageSwitcherData(images, position, showAddButton, placeholderDrawable, groupId, intent, dragDismiss)
        (context as Activity).startActivityForResult(intent, RESULT_IMAGE_SWITCHER)
    }

    private fun addImageSwitcherData(images: List<SquareImage>, position: Int,
                                     showAddButton: Boolean, placeholderDrawable: Int,
                                     groupId: Int, intent: Intent, dragDismiss: Boolean) {
        intent.putParcelableArrayListExtra(NavigatorImage.EXTRA_IMAGE_URL, ArrayList<Parcelable>(images))
        intent.putExtra(EXTRA_IMAGE_DELETE, showAddButton)
        intent.putExtra(EXTRA_IMAGE_URL_POSITION, position)
        intent.putExtra(EXTRA_IMAGE_PLACE_DRAWABLE_ID, placeholderDrawable)
        intent.putExtra(EXTRA_IMAGE_GROUP_ID, groupId)
        intent.putExtra(EXTRA_DRAG_DISMISS, dragDismiss)
    }

    @JvmOverloads fun startCustomAlbumActivity(context: Context, canSelectMaxNum: Int, groupId: Int, type: Int = UIPatternActivity.ALL,gtype:Int) {
        var intent: Intent?
        intent = Intent(getImageGroupIntentAction(context))
        intent.data = Uri.parse(getImageGroupIntentAlbumUri(context))
        if (intent == null || intent.resolveActivity(context.packageManager) == null) {
            intent = Intent(context, AlbumActivity::class.java)
        }
        addAlbumDataWithType(canSelectMaxNum, groupId, type, intent, EXTRA_IMAGE_SELECT_MAX_NUM, EXTRA_IMAGE_GROUP_ID, EXTRA_ALBUM_TYPE,gtype)
        (context as Activity).startActivityForResult(intent, RESULT_SELECT_PHOTOS)
    }

    private fun addAlbumDataWithType(canSelectMaxNum: Int, groupId: Int, type: Int, intent: Intent, extraImageSelectMaxNum: String, extraImageGroupId: String, extraAlbumType: String,gtype:Int) {
        intent.putExtra(extraImageSelectMaxNum, canSelectMaxNum)
        intent.putExtra(extraImageGroupId, groupId)
        intent.putExtra(extraAlbumType, type)
        intent.putExtra("gtype",gtype)
    }

    @JvmOverloads fun startAvatarAlbumActivity(context: Context, groupId: Int, type: Int, aspectRatioX: Int = 1, aspectRatioY: Int = 1) {
        ASPECT_RATIO_X = aspectRatioX
        ASPECT_RATIO_Y = aspectRatioY
        var intent: Intent?
        intent = Intent(getImageGroupIntentAction(context))
        intent.data = Uri.parse(getImageGroupIntentAlbumUri(context))
        if (intent == null || intent.resolveActivity(context.packageManager) == null) {
            intent = Intent(context, AlbumActivity::class.java)
        }
        intent.putExtra(EXTRA_IS_AVATAR_CROP, true)
        addAlbumDataWithType(1, groupId, type, intent, EXTRA_IMAGE_SELECT_MAX_NUM, EXTRA_IMAGE_GROUP_ID, EXTRA_ALBUM_TYPE,0)
        (context as Activity).startActivityForResult(intent, RESULT_SELECT_PHOTOS)
    }

    fun startCropActivity(context: Context, uri: String, isAvatar: Boolean,
                          @ColorRes toolbarColor: Int, @ColorRes statusBarColor: Int) {
        val uCrop = UCrop.of(Uri.parse(uri), Uri.fromFile(File(context.cacheDir, CROPPED_IMAGE_NAME)))

        if (isAvatar) {
            uCrop.withAspectRatio(ASPECT_RATIO_X.toFloat(), ASPECT_RATIO_Y.toFloat())
            val options = UCrop.Options()
            options.setToolbarColor(ContextCompat.getColor(context, toolbarColor))
            options.setStatusBarColor(ContextCompat.getColor(context, statusBarColor))
            options.setToolbarTitle("  ")
            options.setHideBottomControls(true)

            uCrop.withOptions(options)
        }

        uCrop.start(context as Activity)
    }

    private fun getImageGroupIntentAction(context: Context): String {
        return context.getString(R.string.image_group_intent_action)
    }

    private fun getImageGroupIntentSwitcherUri(context: Context): String {
        return context.getString(R.string.image_group_intent_switcher_uri)
    }

    private fun getImageGroupIntentAlbumUri(context: Context): String {
        return context.getString(R.string.image_group_intent_album_uri)
    }
}