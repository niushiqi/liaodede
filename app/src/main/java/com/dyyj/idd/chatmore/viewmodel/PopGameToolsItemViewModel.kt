package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/19
 * desc   :
 */
class PopGameToolsItemViewModel : ViewModel() {

  /**
   * 兑换金币
   */
  fun netConvert2Coin(propNum: String, propId: String) {
    mDataRepository.postConvert2Coin(propNum, propId).subscribe({

                                                                  EventBus.getDefault().post(
                                                                      Convert2CoinVM(it.errorCode == 200, propId = propId, remaomNum = it.data?.remainNum))
                                                                }, {
                                                                  EventBus.getDefault().post(
                                                                      Convert2CoinVM(false, propId = propId))
                                                                })
  }

  class Convert2CoinVM(val isSuucess: Boolean, val remaomNum:Int? = null, val propId: String)
}