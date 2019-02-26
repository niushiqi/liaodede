package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus

class IdentityViewModel : ViewModel() {
    /**
     * 上传身份认证信息
     */
    fun netIdentityInfo(realName:String, idNo:String) {
        val subscribe = mDataRepository.sendIdentityApi(realName, idNo).subscribe(
                { EventBus.getDefault().post(IdetityVM(it.errorCode == 200))},
                { EventBus.getDefault().post(IdetityVM(false))}
        )
        mCompositeDisposable.add(subscribe)
    }

    class IdetityVM(val verify: Boolean)
}