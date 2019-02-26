package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.InviteGameAdapter
import org.greenrobot.eventbus.EventBus

class InviteGameViewModel : ViewModel() {
    private val mAdapter by lazy { InviteGameAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    fun netShareMessage() {
        val subscribe = mDataRepository.getShareMessage("game").subscribe({
            if (it.errorCode == 200) {
//                val index = Math.floor(Math.random() * it.data?.title?.size!!)
            EventBus.getDefault().post(ShareMessageVM(true, it.data?.iconUrl!!, it.data?.inviteUrl!!, it.data?.title!!, it.data?.inviteCode))
        } else {
            EventBus.getDefault().post(ShareMessageVM(false))
        }
        })
        mCompositeDisposable.add(subscribe)
    }

    class ShareMessageVM(val success: Boolean, val icon: String? = "", val shareUrl: String? = "", val title: String? = "", val inviteCode: String? = "")

}