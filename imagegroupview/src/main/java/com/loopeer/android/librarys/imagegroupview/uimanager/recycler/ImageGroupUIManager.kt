package com.loopeer.android.librarys.imagegroupview.uimanager.recycler

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.ViewAnimator
import com.loopeer.android.librarys.imagegroupview.R
import com.loopeer.android.librarys.imagegroupview.adapter.RecyclerViewAdapter
import com.loopeer.android.librarys.imagegroupview.uimanager.IGRecycler
import com.loopeer.android.librarys.imagegroupview.view.CustomPopupView
import com.loopeer.android.librarys.imagegroupview.view.DividerItemImagesDecoration

abstract class ImageGroupUIManager<T>(var context: Context,var igRecycler: IGRecycler<T>): CustomPopupView.FolderItemSelectListener {

    private var mRecyclerView: RecyclerView? = null
    private var mCustomPopupWindowView: CustomPopupView? = null
    private var mViewAnimator: ViewAnimator? = null
    private var mContext:Context?=null
    private var madapter:RecyclerViewAdapter<T>?=null
    protected var mIGRcycler:IGRecycler<T>?=null


    init {
        mContext=context
        mIGRcycler=igRecycler
    }

    private fun setUpView(){
        setUpTextView()
        initView()
        setupRecyclerView()
    }

    private fun setUpTextView() {
        mCustomPopupWindowView?.setNumText(mContext?.getString(R.string.album_all))
//        mCustomPopupWindowView.setFolderItemSelectListener(this)

    }

    protected fun initAdapter(){
        madapter=mIGRcycler?.createRecyclerViewAdapter()
    }

    protected fun setupRecyclerView() {
        mRecyclerView?.layoutManager = GridLayoutManager(mContext, 3)
        mRecyclerView?.addItemDecoration(
            DividerItemImagesDecoration(
                mContext?.resources?.getDimensionPixelSize(R.dimen.inline_padding)!!))
        mRecyclerView?.let {
            it.setPadding(
                mContext?.resources?.getDimensionPixelSize(R.dimen.inline_padding)!! / 2,
                0,
                mContext?.resources?.getDimensionPixelSize(R.dimen.inline_padding)!! / 2,
                0
            )
        }
        mRecyclerView?.adapter = madapter
    }

    protected fun initView() {

    }


}