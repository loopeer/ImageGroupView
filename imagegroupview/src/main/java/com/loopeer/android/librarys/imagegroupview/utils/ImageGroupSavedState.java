package com.loopeer.android.librarys.imagegroupview.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.loopeer.android.librarys.imagegroupview.model.SquareImage;

import java.util.List;

public class ImageGroupSavedState extends View.BaseSavedState {

    private int doingClickViewId;
    private List<SquareImage> squarePhotos;

    public ImageGroupSavedState(Parcelable superState) {
        super(superState);
    }

    public ImageGroupSavedState(Parcel source) {
        super(source);
        doingClickViewId = source.readInt();
        squarePhotos = source.readArrayList(SquareImage.class.getClassLoader());
    }

    public List<SquareImage> getSquarePhotos() {
        return squarePhotos;
    }

    public void setSquarePhotos(List<SquareImage> squarePhotos) {
        this.squarePhotos = squarePhotos;
    }

    public int getDoingClickViewId() {
        return doingClickViewId;
    }

    public void setDoingClickViewId(int doingClickViewId) {
        this.doingClickViewId = doingClickViewId;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(this.doingClickViewId);
        out.writeArray(squarePhotos.toArray());
    }

    public static final Creator<ImageGroupSavedState> CREATOR =
            new Creator<ImageGroupSavedState>() {
                public ImageGroupSavedState createFromParcel(Parcel in) {
                    return new ImageGroupSavedState(in);
                }

                public ImageGroupSavedState[] newArray(int size) {
                    return new ImageGroupSavedState[size];
                }
            };
}
