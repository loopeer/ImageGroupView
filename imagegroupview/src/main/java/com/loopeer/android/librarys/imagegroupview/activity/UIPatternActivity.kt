package com.loopeer.android.librarys.imagegroupview.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.ViewAnimator
import com.loopeer.android.librarys.imagegroupview.adapter.ImageAdapter
import com.loopeer.android.librarys.imagegroupview.uimanager.IGActivityLifecycleCallbacks
import com.loopeer.android.librarys.imagegroupview.uimanager.IGRecycler
import com.loopeer.android.librarys.imagegroupview.uimanager.recycler.ImageGroupUIManager
import com.loopeer.android.librarys.imagegroupview.view.CustomPopupView


class UIPatternActivity: AppCompatActivity() {

    private var mlifecycle:IGActivityLifecycleCallbacks?=null


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        initPatter()
    }

    private fun initPatter() {
//        if (this is IGRecycler<*>) {
//
//        }
    }

}