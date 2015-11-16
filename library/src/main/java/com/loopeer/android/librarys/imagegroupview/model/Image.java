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

}
