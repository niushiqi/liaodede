package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.mqtt.result.RangAwardResult
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/07/08
 * desc   : 随机奖励V2
 */
class RangAwardViewModelV2: ViewModel() {
  private var mRangAwardResult: RangAwardResult? = null


  fun setRangAwardResult(result: RangAwardResult) {
    mRangAwardResult = result
  }


  /**
   * 领取奖励
   */
  fun netGetReward(rewardId: String) {
    val subscribe = mDataRepository.postGetReward(rewardId).subscribe({
                                                                        EventBus.getDefault().post(
                                                                            RewardVM(
                                                                                it.errorCode == 200))
                                                                      }, {
                                                                        EventBus.getDefault().post(
                                                                            RewardVM())
                                                                      })
    mCompositeDisposable.add(subscribe)
  }

  /**
   * 固定奖励
   */
  class RewardVM(val isSuccess: Boolean = false)
}