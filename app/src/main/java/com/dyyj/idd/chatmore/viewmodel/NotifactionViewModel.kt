package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/30
 * desc   :
 */
class NotifactionViewModel:ViewModel() {

  fun getNotifactionStatus(key:String):Boolean {
    return mDataRepository.getNotifactionStatus(key)
  }
}