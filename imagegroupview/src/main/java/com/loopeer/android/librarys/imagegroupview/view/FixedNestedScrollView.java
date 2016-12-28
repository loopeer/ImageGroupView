package com.loopeer.android.librarys.imagegroupview.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

public class FixedNestedScrollView extends NestedScrollView {
    public FixedNestedScrollView(Context context) {
        super(context);
    }

    public FixedNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        dispatchNestedPreScroll(0, dy, consumed, null);
    }
}