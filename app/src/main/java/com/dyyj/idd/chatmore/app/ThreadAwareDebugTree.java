package com.dyyj.idd.chatmore.app;

import timber.log.Timber;

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/26
 * desc   :
 */
public class ThreadAwareDebugTree extends Timber.DebugTree {
  @Override
  protected void log(int priority, String tag, String message, Throwable t) {
    if (tag != null) {
      String threadName = Thread.currentThread().getName();
      tag = "<" + threadName + "> " + tag;
    }
    super.log(priority, tag, message, t);
  }
  @Override
  protected String createStackElementTag(StackTraceElement element) {
    return super.createStackElementTag(element) + "(Line " + element.getLineNumber() + ")";  //日志显示行号
  }
}