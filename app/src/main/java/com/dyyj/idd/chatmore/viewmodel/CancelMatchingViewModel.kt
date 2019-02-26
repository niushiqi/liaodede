package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/20
 * desc   : 取消匹配
 */
class CancelMatchingViewModel:ViewModel() {

  /**
   * 取消匹配
   */
  fun netCancelMatching() {
    val subscribe = mDataRepository.cancelMatching().subscribe({
      EventBus.getDefault().postSticky(CancelMatchingVM(it.errorCode == 200))
      if (it.errorCode != 200) {
        niceToast(it.errorMsg)
      }

    }, {
      EventBus.getDefault().postSticky(CancelMatchingVM(false))
    })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 挂电话
   */
  fun endCall() {
    mDataRepository.endCall()
  }

  class CancelMatchingVM(val isSuccess:Boolean)
}