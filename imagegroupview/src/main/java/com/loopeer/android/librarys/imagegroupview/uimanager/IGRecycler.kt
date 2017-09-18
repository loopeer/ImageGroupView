package com.loopeer.android.librarys.imagegroupview.uimanager

import com.loopeer.android.librarys.imagegroupview.adapter.RecyclerViewAdapter

interface IGRecycler<T>{
    fun createRecyclerViewAdapter():RecyclerViewAdapter<T>
}
