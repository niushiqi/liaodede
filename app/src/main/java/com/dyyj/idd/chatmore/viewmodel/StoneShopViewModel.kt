package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.RecycleShopResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/14
 * desc   : 魔石商城
 */
class StoneShopViewModel : ViewModel() {

  fun netRecycleView() {
    mDataRepository.postShopRecycleData()?.subscribe({
      EventBus.getDefault().post(StoneListVM(obj = it.data?.stone))
      if (it.errorCode != 200) {
        niceToast(it.errorMsg)
      }
    },{
      EventBus.getDefault().post(StoneListVM())
    })
  }

  class StoneListVM(val obj: List<RecycleShopResult.Data.Stone>? = null)

}