package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.SystemMessages
import com.dyyj.idd.chatmore.ui.adapter.InviteNewAdapter
import org.greenrobot.eventbus.EventBus

class InviteNewViewModel : ViewModel() {
    private val mAdapter by lazy { InviteNewAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    fun getSystemMessages() {
        val subscribe = mDataRepository.getSystemMessages().subscribe(
                {
                    if (it.errorCode == 200 && it.data?.size != 0) {
                        EventBus.getDefault().post(SystemMessageVM(it.errorCode == 200, it.data))
                    }
                },
                {}
        )
        mCompositeDisposable.add(subscribe)
    }

    class SystemMessageVM(val success: Boolean, val obj: List<SystemMessages.Data>? = null)
}