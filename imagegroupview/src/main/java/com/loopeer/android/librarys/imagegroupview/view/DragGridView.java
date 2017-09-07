package com.loopeer.android.librarys.imagegroupview.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class DragGridView extends GridView {
    public DragGridView(Context context) {
        this(context,null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }

}
