package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus

class InviteLeftViewModel : ViewModel() {

    fun netInviteCode() {
        val subscribe = mDataRepository.getShareMessage("h5").subscribe({
            if (it.errorCode == 200) {
//                val index = Math.floor(Math.random() * it.data?.title?.size!!)
                try {
                    EventBus.getDefault().post(InviteCodeVM(it.data?.inviteCode!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                EventBus.getDefault().post(InviteCodeVM(""))
            }
        })
        mCompositeDisposable.add(subscribe)
    }

    fun netShareMessage() {
        val subscribe = mDataRepository.getShareMessage("h5").subscribe({
            if (it.errorCode == 200) {
//                val index = Math.floor(Math.random() * it.data?.title?.size!!)
                EventBus.getDefault().post(ShareMessageVM(true, it.data?.iconUrl!!, it.data?.inviteUrl!!, it.data?.title!!, it.data?.inviteCode!!))
            } else {
                EventBus.getDefault().post(ShareMessageVM(false))
            }
        })
        mCompositeDisposable.add(subscribe)
    }

    class ShareMessageVM(val success: Boolean, val icon: String? = "", val shareUrl: String? = "", val title: String? = "", val inviteCode: String? = "")
    class InviteCodeVM(val code: String)

}