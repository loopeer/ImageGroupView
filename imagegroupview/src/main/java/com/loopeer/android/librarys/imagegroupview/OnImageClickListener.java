package com.loopeer.android.librarys.imagegroupview;

import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

import java.util.ArrayList;

public interface OnImageClickListener {
        void onImageClick(SquareImage clickImage, ArrayList<SquareImage> squareImages, ArrayList<String> allImageInternetUrl);
    }