package com.loopeer.android.librarys.imagegroupview.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

import com.loopeer.android.librarys.imagegroupview.activity.AlbumActivity;

public class Album {

    private Intent mAlbumIntent;
    private final Bundle mAlbumOptionBundle;
    private Context mContext;

    public Album(Context context) {
        mContext = context;
        mAlbumIntent = new Intent();
        mAlbumOptionBundle = new Bundle();
    }

    public Album withOptions(Options options) {
        mAlbumOptionBundle.putAll(options.getOptionBundle());
        return this;
    }

    public Intent getAlbumIntent() {
        mAlbumIntent.setClass(mContext, AlbumActivity.class);
        mAlbumIntent.putExtras(mAlbumOptionBundle);
        return mAlbumIntent;
    }

    public Context getContext() {
        return mContext;
    }

    public static class Options {
        private static final String EXTRA_PREFIX = "Album_Activity_Options_";

        public static final String EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + "ToolbarColor";
        public static final String EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + "StatusBarColor";
        public static final String EXTRA_SUBMIT_BUTTON_DRAWABLE = EXTRA_PREFIX + "SubmitButtonDrawable";
        public static final String EXTRA_TOOL_BAR_TITLE = EXTRA_PREFIX + "ToolbarTitle";
        public static final String EXTRA_SUBMIT_BUTTON_TEXT_PREFIX = EXTRA_PREFIX + "SubmitButtonTextPrefix";

        private final Bundle mOptionBundle;

        public Options() {
            mOptionBundle = new Bundle();
        }

        public Bundle getOptionBundle() {
            return mOptionBundle;
        }

        public Options setToolBarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_TOOL_BAR_COLOR, color);
            return this;
        }

        public Options setStatusBarColor(@ColorInt int color) {
            mOptionBundle.putInt(EXTRA_STATUS_BAR_COLOR, color);
            return this;
        }

        public Options setSubmitButtonDrawable(@DrawableRes int drawable) {
            mOptionBundle.putInt(EXTRA_SUBMIT_BUTTON_DRAWABLE, drawable);
            return this;
        }

        public Options setToolBarTitle(String title) {
            mOptionBundle.putString(EXTRA_TOOL_BAR_TITLE, title);
            return this;
        }

        public Options setSubmitButtonTextPrefix(String textPrefix) {
            mOptionBundle.putString(EXTRA_SUBMIT_BUTTON_TEXT_PREFIX, textPrefix);
            return this;
        }

    }

}
