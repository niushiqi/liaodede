package com.dyyj.idd.chatmore.app;

import android.util.Log;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/26
 * desc   :
 */
public class ReleaseTree extends ThreadAwareDebugTree {
  @Override
  protected boolean isLoggable(String tag, int priority) {
    if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
      return false;
    }
    return true;
  }
  @Override
  protected void log(int priority, String tag, String message, Throwable t) {
    if (!isLoggable(tag, priority)) {
      return;
    }
    super.log(priority, tag, message, t);
  }
}