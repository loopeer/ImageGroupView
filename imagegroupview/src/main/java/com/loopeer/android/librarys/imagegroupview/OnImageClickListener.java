package com.loopeer.android.librarys.imagegroupview;

import android.view.View;

import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

public interface OnImageClickListener {
        void onImageClick(View clickImage, SquareImage  squareImage);
    }