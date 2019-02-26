package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.InviteCodeResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/24
 * desc   : 输入兑换码
 */
class RedeemCodeViewModel : ViewModel() {

  /**
   * 输入兑换码
   */
  fun netUseInviteCode(exchangeCode: String) {
    if (exchangeCode.trim().isEmpty()) {
      niceToast("邀请码无效,请重新输入")
      return
    }
    mDataRepository.postExchangeCode(exchangeCode).subscribe({
                                                              EventBus.getDefault().post(
                                                                  InviteCodeVM(it.errorCode == 200,
                                                                               it.data))
                                                            }, {
                                                              EventBus.getDefault().post(
                                                                  InviteCodeVM(false))
                                                            })
  }

  class InviteCodeVM(val isSuccess: Boolean = false, val obj: InviteCodeResult.Data? = null)
}