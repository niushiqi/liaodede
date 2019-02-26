package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.MyGiftsResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/18
 * desc   : 礼物
 */
class GiftsReceivedViewModel : ViewModel() {

  /**
   * 获取礼物数据
   */
  fun netReceivedRose() {
    mDataRepository.getMyGifts().subscribe({
      if(it.data?.size!! > 0)
        EventBus.getDefault().post(ReceivedRoseVM(it))
      else
        niceToast("获取数据为空")
      if (it.errorCode != 200) {
        niceToast(it.errorMsg)
      }
    },{
      EventBus.getDefault().post(ReceivedRoseVM())
    })
  }

  class ReceivedRoseVM(val obj: MyGiftsResult? = null)

}