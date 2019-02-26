package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/17
 * desc   : 随机奖励
 */
class RangAwardViewModel:ViewModel() {

  /**
   * 领取奖励
   */
  fun netGetReward(rewardId: String) {
    mDataRepository.postGetReward(rewardId)
        .subscribe({},{})
  }

  /**
   * 固定奖励
   */
  class RewardVM(val isSuccess: Boolean = false)
}