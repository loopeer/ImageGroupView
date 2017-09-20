package com.loopeer.android.librarys.imagegroupview

import android.view.View
import com.loopeer.android.librarys.imagegroupview.model.SquareImage


interface OnImageClickListener {
    fun onImageClick(clickImage: View, squareImage: SquareImage)
}