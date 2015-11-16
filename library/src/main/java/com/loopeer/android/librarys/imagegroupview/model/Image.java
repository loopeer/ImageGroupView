package com.loopeer.android.librarys.imagegroupview.model;

import java.io.Serializable;

public class Image implements Serializable {

    public String url;
    public String name;
    public long time;

    public Image(String path, String name, long time){
        this.url = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (url != null ? !url.equals(image.url) : image.url != null) return false;
        return !(name != null ? !name.equals(image.name) : image.name != null);

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
