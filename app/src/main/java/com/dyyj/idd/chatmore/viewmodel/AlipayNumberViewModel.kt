package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.GeneralResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : malibo
 * time   : 2018/10/01
 * desc   :
 */
class AlipayNumberViewModel: ViewModel() {
    fun netUpdateAliNum(alipayAccount:String, alipayName:String) {
        mDataRepository.postUpdateAliNum(alipayAccount,alipayName)?.subscribe({
            EventBus.getDefault().post(UpdateAliNumVM(obj = it))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        },{
            EventBus.getDefault().post(UpdateAliNumVM())
        })
    }

    class UpdateAliNumVM(val obj: GeneralResult? = null)

    fun netsummitWithdrow(alipayAccount:String,alipayRealName:String,withdrawNum:String,withdrawType:String) {
        mDataRepository.postSummitWithdraw(alipayAccount,alipayRealName,withdrawNum,withdrawType)?.subscribe({
            EventBus.getDefault().post(SummitWithdrow(obj = it))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        },{
            EventBus.getDefault().post(SummitWithdrow())
        })
    }

    class SummitWithdrow(val obj: GeneralResult? = null)

}