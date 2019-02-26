package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.MatchingTipResult
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/18
 * desc   : 聊天场景,限时开放
 */
class NotOpenPopViewModel : ViewModel() {
  fun netMatchingTip() {
    val subscribe = mDataRepository.getMatchingTip().subscribe({
                                                                 EventBus.getDefault().post(
                                                                     NotOpenVM(it.errorCode == 200,
                                                                               it.data))
                                                               }, {
                                                                 EventBus.getDefault().post(
                                                                     NotOpenVM(false))
                                                               })

    mCompositeDisposable.add(subscribe)
  }

  /**
   * 限时开放数据
   */
  class NotOpenVM(val isSuccess: Boolean = false, val obj: MatchingTipResult.Data? = null)

}