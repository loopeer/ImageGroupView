package com.loopeer.android.librarys.imagegroupview.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageGroupUtils {

    public static String getPathOfPhotoByUri(Context context, Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), uri, filePathColumn);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

}
