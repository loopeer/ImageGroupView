package com.loopeer.android.librarys.imagegroupview;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class SquareImage implements Parcelable {

  public String localUrl;
  public String interNetUrl;
  public String urlKey;
  public PhotoType type;

  public SquareImage(String localUrl, String interNetUrl, String urlKey, PhotoType type) {
    this.localUrl = localUrl;
    this.interNetUrl = interNetUrl;
    this.urlKey = urlKey;
    this.type = type;
  }

  protected SquareImage(Parcel in) {
    localUrl = in.readString();
    interNetUrl = in.readString();
    urlKey = in.readString();
    type = in.readParcelable(PhotoType.class.getClassLoader());
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(localUrl);
    dest.writeString(interNetUrl);
    dest.writeString(urlKey);
    dest.writeParcelable(type, flags);
  }

  public static final Creator<SquareImage> CREATOR = new Creator<SquareImage>() {
    @Override
    public SquareImage createFromParcel(Parcel in) {
      return new SquareImage(in);
    }

    @Override
    public SquareImage[] newArray(int size) {
      return new SquareImage[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  public enum PhotoType implements Parcelable {

    INTER("0"),

    LOCAL("1");


    private String mValue;

    PhotoType(String value) {
      mValue = value;
    }

    @Override
    public String toString() {
      return mValue;
    }

    private static final Map<String, PhotoType>
        STRING_MAPPING = new HashMap<>();

    static {
      for (PhotoType commentType : PhotoType.values()) {
        STRING_MAPPING.put(commentType.toString().toUpperCase(), commentType);
      }
    }

    @SuppressWarnings("unused")
    public static PhotoType fromValue(String value) {
      return STRING_MAPPING.get(value.toUpperCase());
    }

    public static final Creator<PhotoType> CREATOR = new Creator<PhotoType>() {

      public PhotoType createFromParcel(Parcel in) {
        PhotoType option = PhotoType.values()[in.readInt()];
        option.mValue = in.readString();
        return option;
      }

      public PhotoType[] newArray(int size) {
        return new PhotoType[size];
      }

    };

    @Override
    public int describeContents() {
      return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      out.writeInt(ordinal());
      out.writeString(mValue);
    }
  }
}
