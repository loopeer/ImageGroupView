package com.loopeer.android.librarys.imagegroupview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MutipleTouchViewPager extends ViewPager {

  public MutipleTouchViewPager(Context context) {
    super(context);
  }

  public MutipleTouchViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
  private boolean mIsDisallowIntercept = false;
  @Override
  public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    mIsDisallowIntercept = disallowIntercept;
    super.requestDisallowInterceptTouchEvent(disallowIntercept);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
      requestDisallowInterceptTouchEvent(false);
      boolean handled = super.dispatchTouchEvent(ev);
      requestDisallowInterceptTouchEvent(true);
      return handled;
    } else {
      return super.dispatchTouchEvent(ev);
    }
  }

}
