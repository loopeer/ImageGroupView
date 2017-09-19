package com.loopeer.android.librarys.imagegroupview.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.support.annotation.IntDef
import android.support.v4.app.ActivityCompat
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.loopeer.android.librarys.imagegroupview.NavigatorImage
import com.loopeer.android.librarys.imagegroupview.R
import com.loopeer.android.librarys.imagegroupview.adapter.ImageAdapter
import com.loopeer.android.librarys.imagegroupview.adapter.RecyclerViewAdapter
import com.loopeer.android.librarys.imagegroupview.model.Image
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder
import com.loopeer.android.librarys.imagegroupview.uimanager.IGActivityLifecycleCallbacks
import com.loopeer.android.librarys.imagegroupview.uimanager.IGRecycler
import com.loopeer.android.librarys.imagegroupview.uimanager.recycler.IGUIPattern
import com.loopeer.android.librarys.imagegroupview.uimanager.recycler.ImageGroupActivityManager
import com.loopeer.android.librarys.imagegroupview.utils.PermissionUtils
import com.loopeer.android.librarys.imagegroupview.view.CustomPopupView
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


abstract class UIPatternActivity: AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener, ImageAdapter.OnImageClickListener{

    companion object {
        val ALL = 0
        val TAKE_PHOTO = 1
        val ALBUM = 2
    }

    protected val LOADER_ID_FOLDER = 10001


    protected var mManager:IGActivityLifecycleCallbacks?=null

    protected var mMaxSelectedNum: Int = 0
    protected var mImageGroupId: Int = 0
    protected var mAlbumType: Int = 0
    protected var mIsAvatarType: Boolean = false

    private var mSubmitMenu: MenuItem? = null
    private var mTextSubmit: TextView? = null
    protected var mSelectedImages= mutableListOf<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)
        parseIntent()
        checkPermissionToStartAlbum()
    }

    protected fun createUrls(selectedImages: List<Image>): ArrayList<Image> {
        return selectedImages as ArrayList<Image>
    }

    protected fun getCustomPopupWindowView():CustomPopupView?{
        return (mManager as ImageGroupActivityManager<*>).getCustomPopupView()
    }

    private fun checkPermissionToStartAlbum() {
        var s=!PermissionUtils.hasSelfPermissions(this, *NavigatorImage.PERMISSION_WRITE_STARTREQUEST)
        Log.i("mlx","Permissions:$s")
        if (!PermissionUtils.hasSelfPermissions(this, *NavigatorImage.PERMISSION_WRITE_STARTREQUEST)) {
            Log.i("mlx","requestPermissions")
            ActivityCompat.requestPermissions(this, NavigatorImage.PERMISSION_WRITE_STARTREQUEST,NavigatorImage.REQUEST_WRITE_STARTREQUEST)
        } else {
            if (!PermissionUtils.hasSelfPermissions(this, *NavigatorImage.PERMISSION_CAMERA_STARTREQUEST)) {
                ActivityCompat.requestPermissions(this, NavigatorImage.PERMISSION_CAMERA_STARTREQUEST, NavigatorImage.REQUEST_CAMERA_STARTREQUEST)
            } else {
                initPatter(this)
            }
        }
    }

    protected fun createFoldersWithAllImageFolder(folders: MutableList<ImageFolder>): List<*> {
        if (folders.size > 0) {
            val folder = ImageFolder()
            folder.name = resources.getString(R.string.album_all)
            folder.dir = null
            folder.firstImagePath = folders[0].firstImagePath
            var imageCount = 0
            for (imageFolder in folders) {
                imageCount += imageFolder.count
                folder.images.addAll(imageFolder.images)
            }
            folder.count = imageCount
            folders.add(0, folder)
        }
        return folders
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_image_group_submit, menu)
        if (mAlbumType != TAKE_PHOTO) updateSubmitMenu(menu)
        return true
    }

    private fun updateSubmitMenu(menuItem: Menu) {
        mSubmitMenu = menuItem.findItem(R.id.action_submit)
        val view = mSubmitMenu?.actionView
        mTextSubmit = view?.findViewById(R.id.text_image_submit) as TextView
        if (mIsAvatarType) {
            mTextSubmit?.visibility = View.INVISIBLE
        }
        mTextSubmit?.setOnClickListener(this)
        updateSubmitText()
    }

    protected fun updateSubmitText() {
        mTextSubmit?.isEnabled = mSelectedImages.size > 0
        mTextSubmit?.text = getSubmitText()
    }

    private fun getSubmitText(): String {
        return if (mSelectedImages.size == 0)
            resources.getString(R.string.action_submit)
        else if (mMaxSelectedNum == 0)
            resources.getString(R.string.action_submit_string_no_max, mSelectedImages.size)
        else
            resources.getString(R.string.action_submit_string, mSelectedImages.size, mMaxSelectedNum)
    }



    private fun parseIntent() {
        val intent = intent
        mImageGroupId = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, 0)
        mMaxSelectedNum = intent.getIntExtra(NavigatorImage.EXTRA_IMAGE_SELECT_MAX_NUM, 0)
        mAlbumType = intent.getIntExtra(NavigatorImage.EXTRA_ALBUM_TYPE, 0)
        mIsAvatarType = intent.getBooleanExtra(NavigatorImage.EXTRA_IS_AVATAR_CROP, false)
    }

    private fun initPatter(context: Context) {
        if (context !is IGUIPattern) return
        if (mAlbumType != TAKE_PHOTO) {
            if(context is IGRecycler<*>){
                delegate.setContentView(R.layout.activity_album)
                setUIManager(ImageGroupActivityManager(context,context))
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportLoaderManager.initLoader<Cursor>(LOADER_ID_FOLDER, null, this)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val SDState = Environment.getExternalStorageState()
        if (SDState == Environment.MEDIA_MOUNTED) {
            ActivityCompat.startActivityForResult(this,
                Intent(this, UserCameraActivity::class.java), NavigatorImage.RESULT_TAKE_PHOTO,
                null)
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUIManager(igActivityLifecycleCallbacks: IGActivityLifecycleCallbacks) {
        mManager=igActivityLifecycleCallbacks
        mManager?.onCreated()
    }

    override fun onDestroy() {
        mManager?.onDestroyed()
        super.onDestroy()
    }

    override fun onImageSelected(image: Image?, position: Int): Int {
        val index: Int
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image)
            index = 2
        } else if (mSelectedImages.size == mMaxSelectedNum && mMaxSelectedNum != 0) {
            return 0
        } else {
            if (mIsAvatarType) {
                startCrop(image?.url!!)
                return 0
            }
            image?.time = System.currentTimeMillis()
            mSelectedImages.add(image!!)
            index = 1
        }
        adapterUpdateSelect((mManager as ImageGroupActivityManager<*>).getAdapter()!!,mSelectedImages,position)
        updateSubmitText()
        return index
    }

    abstract fun adapterUpdateSelect(recyclerViewAdapter: RecyclerViewAdapter<*>, mSelectedImages: MutableList<Image>, position: Int)

    override fun onClick(v: View?) {
        finishWithResult()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        doParseData(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    abstract fun doParseData(data: Cursor)

    abstract fun finishWithResult()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(this, requestCode, grantResults)
    }

    override fun onCameraSelected() {
        startCamera()
    }

    fun onRequestPermissionsResult(target: Activity, requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            NavigatorImage.REQUEST_CAMERA_STARTREQUEST -> {
                if (PermissionUtils.getTargetSdkVersion(target) < 23 && !PermissionUtils.hasSelfPermissions(target, *NavigatorImage.PERMISSION_CAMERA_STARTREQUEST)) {
                    finish()
                    return
                }
                if (PermissionUtils.verifyPermissions(*grantResults)) {
                    initPatter(this)
                } else {
                    if (!PermissionUtils.shouldShowRequestPermissionRationale(target, *NavigatorImage.PERMISSION_CAMERA_STARTREQUEST)) {
                        Toast.makeText(this, com.loopeer.android.librarys.imagegroupview.R.string.camera_permission_setting_reject, Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
            NavigatorImage.REQUEST_WRITE_STARTREQUEST -> {
                if (PermissionUtils.getTargetSdkVersion(target) < 23 && !PermissionUtils.hasSelfPermissions(target, *NavigatorImage.PERMISSION_WRITE_STARTREQUEST)) {
                    finish()
                    return
                }
                if (PermissionUtils.verifyPermissions(*grantResults)) {
                    if (!PermissionUtils.hasSelfPermissions(this, *NavigatorImage.PERMISSION_CAMERA_STARTREQUEST)) {
                        ActivityCompat.requestPermissions(this, NavigatorImage.PERMISSION_CAMERA_STARTREQUEST, NavigatorImage.REQUEST_CAMERA_STARTREQUEST)
                    } else {
                        initPatter(this)
                    }
                } else {
                    if (!PermissionUtils.shouldShowRequestPermissionRationale(target, *NavigatorImage.PERMISSION_WRITE_STARTREQUEST)) {
                        Toast.makeText(this, com.loopeer.android.librarys.imagegroupview.R.string.camera_permission_setting_reject, Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
            else -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null || resultCode != Activity.RESULT_OK && mAlbumType == TAKE_PHOTO) {
            this.finish()
        }
        if (data == null || resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                val resultUri = UCrop.getOutput(data)
                if (resultUri != null) {
                    mSelectedImages.clear()
                    mSelectedImages.add(Image(copyFileToDownloads(resultUri), "", System.currentTimeMillis()))
                    finishWithResult()
                } else {
                    finish()
                }
            }
            else -> {
                val photoTakeUrl = data.getStringExtra(NavigatorImage.EXTRA_PHOTO_URL)
                if (requestCode == NavigatorImage.RESULT_TAKE_PHOTO && null != photoTakeUrl) {
                    mSelectedImages.add(Image(photoTakeUrl, "", System.currentTimeMillis()))
                    if (mIsAvatarType) {
                        startCrop(photoTakeUrl)
                    } else {
                        finishWithResult()
                    }
                } else {
                    finishWithResult()
                }
            }
        }
    }

    private fun startCrop(url: String) {
        NavigatorImage.startCropActivity(this, "file://" + url, true,
            R.color.image_group_theme_primary, R.color.image_group_theme_primary_dark)
    }

    private fun copyFileToDownloads(croppedFileUri: Uri): String? {
        val downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val filename = String.format("%d_%s", Calendar.getInstance().timeInMillis, croppedFileUri.lastPathSegment)

        val saveFile = File(downloadsDirectoryPath, filename)

        var inStream: FileInputStream? = null
        try {
            inStream = FileInputStream(File(croppedFileUri.path))
            val outStream = FileOutputStream(saveFile)
            val inChannel = inStream.channel
            val outChannel = outStream.channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
            inStream.close()
            outStream.close()
            return saveFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

}