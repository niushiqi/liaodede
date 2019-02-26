package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.CovertGoldResult
import com.dyyj.idd.chatmore.model.network.result.GoldResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class CovertGoldViewModel : ViewModel() {

  var data: CovertGoldResult.Data? = null

  fun netConvertGold(gold: Int, first: Boolean) {
    val subscribe = mDataRepository.getConvertGold(gold).subscribe({
                                                                     if (it.errorCode == 200) {
                                                                       data = it.data
                                                                       EventBus.getDefault().post(
                                                                           ConvertGoldVM(true,
                                                                                         first,
                                                                                         it.data))
                                                                     } else {
                                                                       EventBus.getDefault().post(
                                                                           ConvertGoldVM(false,
                                                                                         first))
                                                                     }
                                                                     if (it.errorCode != 200) {
                                                                       niceToast(it.errorMsg)
                                                                     }
                                                                   }, {
                                                                     EventBus.getDefault().post(
                                                                         ConvertGoldVM(false,
                                                                                       first))
                                                                   })
    mCompositeDisposable.add(subscribe)
  }

  fun netConvert(gold: Int) {
    val subscribe = mDataRepository.ConvertGold(gold).subscribe({
                                                                  EventBus.getDefault().post(
                                                                      ConvertVM(it.errorCode == 200,
                                                                                it.data))
                                                                  if (it.errorCode != 200) {
                                                                    niceToast(it.errorMsg)
                                                                  }
                                                                }, {
                                                                  EventBus.getDefault().post(
                                                                      ConvertVM(false))
                                                                })
    mCompositeDisposable.add(subscribe)
  }

  class ConvertGoldVM(val success: Boolean, val first: Boolean,
      val obj: CovertGoldResult.Data? = null)

  class ConvertVM(val success: Boolean, val obj: GoldResult.Data? = null)

}