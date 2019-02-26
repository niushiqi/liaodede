package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/17
 * desc   : 固定奖励
 */
class CommonAwardViewModel : ViewModel() {

  /**
   * 领取奖励
   */
  fun netGetReward(rewardId: String) {
    mDataRepository.postGetReward(rewardId).subscribe({
                                                        EventBus.getDefault().post(
                                                            RewardVM(it.errorCode == 200))
        if (it.errorCode == 200) {
            EventBus.getDefault().post(CallViewModel.RefreshWalletCallFragment())
        }
                                                      }, {
                                                        EventBus.getDefault().post(RewardVM())
                                                      })
  }

  /**
   * 固定奖励
   */
  class RewardVM(val isSuccess: Boolean = false)
}