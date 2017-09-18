package com.loopeer.android.librarys.imagegroupview.uimanager.recycler

import android.content.Context
import android.view.View
import android.widget.ViewAnimator
import com.loopeer.android.librarys.imagegroupview.R
import com.loopeer.android.librarys.imagegroupview.uimanager.IGLoadHelper


class IGLoad (context: Context,viewAnimator: ViewAnimator) :IGLoadHelper{

    private val mEmptyIndex: Int
    private val mProgressIndex: Int
    private val mContentIndex: Int

    var mContext:Context
    var mViewAnimator:ViewAnimator

    init {
        mContext=context
        mViewAnimator=viewAnimator

        mEmptyIndex=mViewAnimator.indexOfChild(mViewAnimator.findViewById(R.id.text_album_empty))
        mProgressIndex=mViewAnimator.indexOfChild(mViewAnimator.findViewById(R.id.progress_album))
        mContentIndex=mViewAnimator.indexOfChild(mViewAnimator.findViewById(R.id.recycler_album))
    }

    override fun getContentView(): View {
        return mViewAnimator.getChildAt(mContentIndex)
    }

    override fun showProgress() {
        mViewAnimator.displayedChild=mProgressIndex
    }

    override fun showContent() {
        mViewAnimator.displayedChild = mContentIndex
    }

    override fun showEmpty() {
        mViewAnimator.displayedChild = mEmptyIndex
    }


}