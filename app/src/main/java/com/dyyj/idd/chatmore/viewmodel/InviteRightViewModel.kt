package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.MyTaskResult
import com.dyyj.idd.chatmore.ui.adapter.InviteCenterAdapter
import org.greenrobot.eventbus.EventBus

class InviteRightViewModel: ViewModel() {
    private val mAdapter by lazy { InviteCenterAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    fun netMyTasks() {
        var subscribe = mDataRepository.getMyTasks().subscribe(
                { EventBus.getDefault().post(MyTasksVM(it.errorCode == 200, it.data)) }
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netShareMessage() {
        val subscribe = mDataRepository.getShareMessage("h5").subscribe({
            if (it.errorCode == 200) {
//                val index = Math.floor(Math.random() * it.data?.title?.size!!)
                EventBus.getDefault().post(ShareMessageVM(true, it.data?.iconUrl!!, it.data?.inviteUrl!!, it.data?.title!!, it.data?.inviteCode))
            } else {
                EventBus.getDefault().post(ShareMessageVM(false))
            }
        })
        mCompositeDisposable.add(subscribe)
    }

    class MyTasksVM(var success: Boolean, val obj: MyTaskResult.Data? = null)
    class ShareMessageVM(val success: Boolean, val icon: String? = "", val shareUrl: String? = "", val title: String? = "", val inviteCode: String? = "")
}