package com.loopeer.android.apps.imagegroupview;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class ImageGroupApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
