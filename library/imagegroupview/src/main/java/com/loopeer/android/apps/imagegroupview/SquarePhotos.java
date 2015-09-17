package com.loopeer.android.apps.imagegroupview;

import java.util.HashMap;
import java.util.Map;

public class SquarePhotos {

  public String url;
  public PhotoType type;

  public SquarePhotos(){}

  public SquarePhotos(String url, PhotoType type) {
    this.url = url;
    this.type = type;
  }

  public enum PhotoType {

    INTER("0"),

    LOCAL("1");


    private final String mValue;

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

    public static PhotoType fromValue(String value) {
      return STRING_MAPPING.get(value.toUpperCase());
    }
  }
}
