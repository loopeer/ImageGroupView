package com.loopeer.android.librarys.imagegroupview.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.loopeer.android.librarys.imagegroupview.NavigatorImage
import com.loopeer.android.librarys.imagegroupview.adapter.ImageAdapter
import com.loopeer.android.librarys.imagegroupview.adapter.RecyclerViewAdapter
import com.loopeer.android.librarys.imagegroupview.model.Image
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder
import com.loopeer.android.librarys.imagegroupview.uimanager.IGRecycler
import java.io.File
import java.util.ArrayList


class AlbumActivity : UIPatternActivity(),IGRecycler<Image> {


    override fun adapterUpdateContentView(recyclerViewAdapter: RecyclerViewAdapter<*>, folder: ImageFolder?) {
        var mImageAdapter = (recyclerViewAdapter as ImageAdapter)
        mImageAdapter.setAlbumType(mAlbumType)
        mImageAdapter.setIsAvatarType(mIsAvatarType)
        mImageAdapter.updateFolderImageData(folder)
    }

    override fun adapterUpdateSelect(recyclerViewAdapter: RecyclerViewAdapter<*>, mSelectedImages: MutableList<Image>, position: Int) {
        (recyclerViewAdapter as ImageAdapter).updateSelectImages(mSelectedImages, position)
    }


    override fun createRecyclerViewAdapter(): RecyclerViewAdapter<Image> {
        val madapter=ImageAdapter(this)
        madapter.setOnImageClickListener(this)
        return madapter
    }

    @SuppressLint("StaticFieldLeak")
    override fun doParseData(data: Cursor) {

        object : AsyncTask<Cursor, Void, MutableList<ImageFolder>>() {

            override fun doInBackground(vararg params: Cursor): MutableList<ImageFolder> {
                val data = params[0]
                if (data != null) {
                    val folders = mutableListOf<ImageFolder>()
                    val count = data.count
                    if (count > 0) {
                        data.moveToFirst()
                        do {
                            val path = data.getString(data.getColumnIndexOrThrow(NavigatorImage.IMAGE_PROJECTION[0]))
                            val name = data.getString(data.getColumnIndexOrThrow(NavigatorImage.IMAGE_PROJECTION[1]))
                            val dateTime = data.getLong(data.getColumnIndexOrThrow(NavigatorImage.IMAGE_PROJECTION[2]))
                            val image = Image(path, name, dateTime)

                            val imageFile = File(path)
                            val folderFile = imageFile.parentFile
                            val folder = ImageFolder()
                            folder.name = folderFile.name
                            folder.dir = folderFile.absolutePath
                            folder.firstImagePath = path
                            if (!folders.contains(folder)) {
                                val imageList = ArrayList<Image>()
                                imageList.add(image)
                                folder.count++
                                folder.images = imageList
                                folders.add(folder)
                            } else {
                                val f = folders.get(folders.indexOf(folder))
                                f.images.add(image)
                                f.count++
                            }
                        } while (data.moveToNext())

                        return folders
                    }
                }

                return mutableListOf<ImageFolder>()
            }

            override fun onPostExecute(list: MutableList<ImageFolder>) {
                super.onPostExecute(list)
                getCustomPopupWindowView()?.updateFolderData(createFoldersWithAllImageFolder(list))
            }
        }.execute(data)
    }

    override fun finishWithResult() {
        val intent = intent
        intent.putExtra(NavigatorImage.EXTRA_PHOTOS_URL, createUrls(mSelectedImages))
        intent.putExtra(NavigatorImage.EXTRA_IMAGE_GROUP_ID, mImageGroupId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val cursorLoader = CursorLoader(this,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, NavigatorImage.IMAGE_PROJECTION, null, null, NavigatorImage.IMAGE_PROJECTION[2] + " DESC")
        return cursorLoader
    }


}
