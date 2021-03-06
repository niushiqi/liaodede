package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.GetGiftResult
import org.greenrobot.eventbus.EventBus


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/18
 * desc   : 领取红包
 */
class RedPacketPopViewModel2 : ViewModel() {

  var mData: GetGiftResult.Data? = null

  /**
   * 领取红包
   */
  fun netGetGift(giftId: String) {
    val subscribe = mDataRepository.postGetGift(giftId).subscribe({
                                                                    if (it.errorCode == 200) {
                                                                      mData = it.data
                                                                    }
                                                                    EventBus.getDefault().post(
                                                                        GetGiftVM(
                                                                            isSuccess = it.errorCode == 200,
                                                                            obj = it.data))
                                                                  }, {

                                                                    EventBus.getDefault().post(
                                                                        GetGiftVM(
                                                                            isSuccess = false))
                                                                  })
    mCompositeDisposable.add(subscribe)
  }


  /**
   * 红包
   */
  class GetGiftVM(val isSuccess: Boolean = false, val obj: GetGiftResult.Data? = null)

  /**
   * 领取红包
   */
  class GetGiftOkVM(val obj: GetGiftResult.Data? = null)
}