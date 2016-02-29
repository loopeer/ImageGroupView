package com.loopeer.android.librarys.imagegroupview.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class SquareImage implements Parcelable {

  public int id;
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

  public void setId(int id) {
    this.id = id;
  }

  protected SquareImage(Parcel in) {
    id = in.readInt();
    localUrl = in.readString();
    interNetUrl = in.readString();
    urlKey = in.readString();
    type = in.readParcelable(PhotoType.class.getClassLoader());
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
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

    NETWORK("0"),

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SquareImage that = (SquareImage) o;

    if (id != that.id) return false;
    if (localUrl != null ? !localUrl.equals(that.localUrl) : that.localUrl != null) return false;
    if (interNetUrl != null ? !interNetUrl.equals(that.interNetUrl) : that.interNetUrl != null)
      return false;
    if (urlKey != null ? !urlKey.equals(that.urlKey) : that.urlKey != null) return false;
    return type == that.type;

  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (localUrl != null ? localUrl.hashCode() : 0);
    result = 31 * result + (interNetUrl != null ? interNetUrl.hashCode() : 0);
    result = 31 * result + (urlKey != null ? urlKey.hashCode() : 0);
    result = 31 * result + type.hashCode();
    return result;
  }
}
