package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/19
 * desc   :
 */
class PopGiftsItemViewModel : ViewModel() {

  /**
   * 兑换金币
   */
  fun netExchange2Coin(propNum: String, propId: String) {
    mDataRepository.exchange2coin(propNum, propId).subscribe({
          EventBus.getDefault().post(
                  Exchange2CoinVM(it.errorCode == 200, remaomNum = it.data?.remainNum))
        }, {
            niceToast(it.message)
        })
  }

  class Exchange2CoinVM(val isSuucess: Boolean, val remaomNum:Int? = null)
}