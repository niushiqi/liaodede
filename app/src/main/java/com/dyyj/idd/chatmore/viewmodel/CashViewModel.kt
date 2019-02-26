package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.CashSumaryResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/23
 * desc   :
 */
class CashViewModel:ViewModel() {
//  private val mAdapter by lazy { CashAdapter() }

  /**
   * 获取Adapter
   */
//  fun getAdapter() = mAdapter

  /**
   * 现金概要
   */
  fun netWalletCash() {
    mDataRepository.postCashSummary()
        .subscribe({
          EventBus.getDefault().post(CashVM(obj = it.data))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
                   },{
          EventBus.getDefault().post(CashVM())
        })
  }

  class CashVM(val obj:CashSumaryResult.Data? = null)
}