package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.MyHeBaoResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * update : zwj
 * e-mail : none
 * time   : 2019/01/23
 * desc   : 道具
 */

class GameToolsInFrgViewModel : ViewModel() {

  /**
   * 查看我的资产
   */
  fun ownPropNum() {
    val subscribe = mDataRepository.ownPropNum().subscribe({
                                                            EventBus.getDefault().post(
                                                                GameToolsVM(it.errorCode == 200,
                                                                            it.data))
                                                          }, {
                                                            niceToast(it.message)
                                                          })
    mCompositeDisposable.add(subscribe)
  }

  class GameToolsVM(val isSuccess: Boolean, val obj: MyHeBaoResult.Data? = null)
}