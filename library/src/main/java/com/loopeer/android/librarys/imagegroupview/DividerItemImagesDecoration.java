package com.loopeer.android.librarys.imagegroupview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerItemImagesDecoration extends RecyclerView.ItemDecoration {

  private int mInsets;

  public DividerItemImagesDecoration(int insets) {
    mInsets = insets;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    outRect.set(mInsets / 2, mInsets, mInsets / 2, 0);
  }

}
