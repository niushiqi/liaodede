package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.WithdrawCheckResult
import com.dyyj.idd.chatmore.model.network.result.WithdrawSumaryResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/16
 * desc   : 提现
 */
class WithdrawfViewModel: ViewModel() {

    fun netWithdrawCheck(type:String,num:String) {
        mDataRepository.postWithdrawCheck(type,num)?.subscribe({
            EventBus.getDefault().post(WithdrawCheckVM(obj = it.data))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        },{
            EventBus.getDefault().post(WithdrawCheckVM())
        })
    }



    fun netCash(type:String) {
        mDataRepository.postWidthdrawSummary(type)?.subscribe({
            EventBus.getDefault().post(CashVM(obj = it.data))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        },{
            EventBus.getDefault().post(CashVM())
        })
    }

    class CashVM(val obj: WithdrawSumaryResult.Data? = null)
    class WithdrawCheckVM(val obj: WithdrawCheckResult.Data? = null)

}