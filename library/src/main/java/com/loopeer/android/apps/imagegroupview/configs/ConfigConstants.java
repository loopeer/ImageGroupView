/**
 * Created by YuGang Yang on April 07, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package com.loopeer.android.apps.imagegroupview.configs;

import com.facebook.common.util.ByteConstants;

public class ConfigConstants {
  private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

  public static final int MAX_DISK_CACHE_SIZE = 40 * ByteConstants.MB;
  public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
  public static final int MAX_SEND_CAPTCHA_COUNT_DOWN = 60 ;

}
