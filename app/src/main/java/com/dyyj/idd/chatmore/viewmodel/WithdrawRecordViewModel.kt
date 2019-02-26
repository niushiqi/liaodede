package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.PayHistoryResult
import com.dyyj.idd.chatmore.model.network.result.WithdrawRecordResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : malibo
 * time   : 2018/10/01
 * desc   :
 */
class WithdrawRecordViewModel: ViewModel() {

    fun netRecord() {
        mDataRepository.postWidthdrawRecord()?.subscribe({
            EventBus.getDefault().post(RecordVM(obj = it.data))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        },{
            EventBus.getDefault().post(RecordVM())
        })
    }

    class RecordVM(val obj: List<WithdrawRecordResult.recordData>? = null)

    fun netPayHistory() {
        mDataRepository.postPayHistory()?.subscribe({
            EventBus.getDefault().post(PayHistoryVM(obj = it.data))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        },{
            EventBus.getDefault().post(PayHistoryVM())
        })
    }

    class PayHistoryVM(val obj: List<PayHistoryResult.recordData>? = null)

}