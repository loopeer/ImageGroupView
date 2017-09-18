package com.loopeer.android.librarys.imagegroupview.uimanager.recycler

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ViewAnimator
import com.loopeer.android.librarys.imagegroupview.R
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder
import com.loopeer.android.librarys.imagegroupview.uimanager.IGActivityLifecycleCallbacks
import com.loopeer.android.librarys.imagegroupview.uimanager.IGRecycler


open class ImageGroupActivityManager<T>(context: Context, igRecycler: IGRecycler<T>): ImageGroupUIManager<T>(context,igRecycler),IGActivityLifecycleCallbacks{

    protected var mLoad:IGLoad?=null

    override fun setUpLoadHelper(view: View) {
        mLoad=IGLoad(mContext!!,(mContext as Activity).findViewById(R.id.view_album_animator) as ViewAnimator)
    }


    override fun onCreated() {
    }

    override fun onPostCreated() {
        setUpLoadHelper(null!!)
        initAdapter()
        setUpView()
    }

    override fun onStarted() {
    }

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    override fun onStopped() {
    }

    override fun onDestroyed() {
        destroyAdapter()
        cleanRecyclerView()
    }


    override fun isFinishing(): Boolean {
        return (mContext as Activity).isFinishing
    }

    override fun onFolderItemSelected(imageFolder: ImageFolder?) {
    }
}