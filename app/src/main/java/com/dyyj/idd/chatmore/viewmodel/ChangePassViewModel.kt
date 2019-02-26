package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class ChangePassViewModel : ViewModel() {
    fun netChangeApi(oldPass: String, newPass: String) {
        val subscribe = mDataRepository.ChangePass(oldPass, newPass).subscribe(
                {
                    EventBus.getDefault().post(ChangePassVM(it.errorCode == 200))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                },
                {EventBus.getDefault().post(ChangePassVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    class ChangePassVM(val success: Boolean);
}