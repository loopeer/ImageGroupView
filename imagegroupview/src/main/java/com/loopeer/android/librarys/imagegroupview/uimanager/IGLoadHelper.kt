package com.loopeer.android.librarys.imagegroupview.uimanager

import android.view.View

interface IGLoadHelper {

    fun getContentView(): View

    fun showProgress()

    fun showContent()

    fun showEmpty()


}