package com.loopeer.android.librarys.imagegroupview.uimanager.recycler

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ViewAnimator
import com.loopeer.android.librarys.imagegroupview.R
import com.loopeer.android.librarys.imagegroupview.adapter.RecyclerViewAdapter
import com.loopeer.android.librarys.imagegroupview.uimanager.IGLoadHelper
import com.loopeer.android.librarys.imagegroupview.uimanager.IGRecycler
import com.loopeer.android.librarys.imagegroupview.view.CustomPopupView
import com.loopeer.android.librarys.imagegroupview.view.DividerItemImagesDecoration

abstract class ImageGroupUIManager<T>(context: Context,igRecycler: IGRecycler<T>): CustomPopupView.FolderItemSelectListener {

    private var mRecyclerView: RecyclerView? = null
    private var mCustomPopupWindowView: CustomPopupView? = null
    private var mViewAnimator: ViewAnimator? = null
    var mContext: Context?
    private var madapter:RecyclerViewAdapter<T>?=null
    protected var mIGRcycler:IGRecycler<T>?=null
    protected var mItems= mutableListOf<T>()
    protected var mLoadHelper:IGLoadHelper?=null


    init {
        mContext=context
        mIGRcycler=igRecycler
    }

    protected fun setUpView(){
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

    protected abstract fun isFinishing(): Boolean

    protected abstract fun setUpLoadHelper(view:View)

    fun updateRecyclerView() {
        if (isFinishing()) return

//        madapter?.updateData(mItems)
//        if (mItems?.isEmpty()!!) {
//            if (!NetworkUtils.checkNetAvailable(mContext)) {
//                mLoadHelper.showNetError()
//            } else {
//                mLoadHelper.showEmpty()
//            }
//        } else {
//            mLoadHelper.showContent()
//        }
    }

    private fun setupRecyclerView() {
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

    private fun initView() {
        mRecyclerView= mLoadHelper?.getContentView() as RecyclerView?
    }

    fun destroyAdapter() {
        madapter = null
    }

    fun cleanRecyclerView() {
        mRecyclerView?.adapter = null
        mRecyclerView?.layoutManager = null
        mRecyclerView=null
    }

}