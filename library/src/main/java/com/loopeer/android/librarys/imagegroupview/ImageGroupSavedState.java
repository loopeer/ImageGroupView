package com.loopeer.android.librarys.imagegroupview;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;

public class ImageGroupSavedState extends View.BaseSavedState {

    private int doingClickViewId;
    private ArrayList<SquareImage> squarePhotos;

    public ImageGroupSavedState(Parcelable superState) {
        super(superState);
    }

    public ImageGroupSavedState(Parcel source) {
        super(source);
        doingClickViewId = source.readInt();
        squarePhotos = source.readArrayList(SquareImage.class.getClassLoader());
    }

    public ArrayList<SquareImage> getSquarePhotos() {
        return squarePhotos;
    }

    public void setSquarePhotos(ArrayList<SquareImage> squarePhotos) {
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
