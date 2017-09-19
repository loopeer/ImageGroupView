package com.loopeer.android.librarys.imagegroupview.uimanager

import com.loopeer.android.librarys.imagegroupview.adapter.RecyclerViewAdapter
import com.loopeer.android.librarys.imagegroupview.model.ImageFolder
import com.loopeer.android.librarys.imagegroupview.uimanager.recycler.IGUIPattern

interface IGRecycler<T>:IGUIPattern{
    fun createRecyclerViewAdapter():RecyclerViewAdapter<T>
    fun adapterUpdateContentView(recyclerViewAdapter: RecyclerViewAdapter<*>, folder: ImageFolder?)
}
