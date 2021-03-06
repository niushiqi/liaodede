package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.RecycleShopResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/14
 * desc   : 匹配卡商城
 */
class MatchingCardShopViewModel : ViewModel() {

  fun netRecycleView() {
    mDataRepository.postShopRecycleData()?.subscribe({
      EventBus.getDefault().post(MatchCardListVM(obj = it.data?.matchCard))
      if (it.errorCode != 200) {
        niceToast(it.errorMsg)
      }
    },{
      niceToast(it.message)
    })
  }

  class MatchCardListVM(val obj: List<RecycleShopResult.Data.MatchCard>? = null)

}