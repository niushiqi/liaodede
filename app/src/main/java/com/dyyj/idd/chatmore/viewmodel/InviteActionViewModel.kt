package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.MyInviteResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class InviteActionViewModel : ViewModel() {

    var inviteData: MyInviteResult.Data? = null

    fun netInviteCode() {
        val subscribe = mDataRepository.getMyInvite().subscribe(
                {
                    if (it.errorCode == 200) {
                        inviteData = it.data
                        EventBus.getDefault().post(MyInviteVM(true, it.data))
                    } else {
                        niceToast(it.errorMsg)
                    }
                },
                {EventBus.getDefault().post(MyInviteVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    class MyInviteVM(val success: Boolean, val obj: MyInviteResult.Data? = null)
}