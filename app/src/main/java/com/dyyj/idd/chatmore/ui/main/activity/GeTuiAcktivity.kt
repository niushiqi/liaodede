package com.dyyj.idd.chatmore.ui.main.activity

import android.app.Activity
import android.os.Bundle
import com.igexin.sdk.GTServiceManager

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/23
 * desc   :
 */
class GeTuiAcktivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //调 个推GTServiceManager的onActivityCreate 法。必须传递this
    GTServiceManager.getInstance().onActivityCreate(this);
  }
}