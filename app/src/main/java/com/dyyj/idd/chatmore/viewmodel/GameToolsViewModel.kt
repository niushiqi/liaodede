package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.GameToolsResult
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/19
 * desc   : 道具
 */
class GameToolsViewModel : ViewModel() {

  /**
   * 获取道具数据
   */
  fun netIndex() {
    val subscribe = mDataRepository.postIndex().subscribe({
                                                            Timber.tag("").i("")
                                                            EventBus.getDefault().post(
                                                                GameToolsVM(it.errorCode == 200,
                                                                            it.data))
                                                          }, {
                                                            Timber.tag("viewmodel").i(it.message)
                                                            EventBus.getDefault().post(
                                                                GameToolsVM(false))
                                                          })
    mCompositeDisposable.add(subscribe)
  }

  class GameToolsVM(val isSuccess: Boolean, val obj: GameToolsResult.Data? = null)
}