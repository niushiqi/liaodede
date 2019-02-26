package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.MySharedRoseResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/18
 * desc   : 礼物
 */
class ReceivedGiftsViewModel : ViewModel() {

  fun netMySharedRose() {
    mDataRepository.getMyGiftLogs().subscribe({
      if (it.data?.receive != null) {
        EventBus.getDefault().post(MySharedRoseVM(it.data.receive))
      }else{
        niceToast(it.errorMsg)
      }
      if (it.errorCode != 200) {
        niceToast(it.errorMsg)
      }
    },{
      EventBus.getDefault().post(MySharedRoseVM())
    })
  }

  class MySharedRoseVM(val obj: List<MySharedRoseResult.Data.Gift?> = listOf())


}