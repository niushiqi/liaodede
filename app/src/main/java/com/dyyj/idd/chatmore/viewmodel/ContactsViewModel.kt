package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.ContactsResult
import com.dyyj.idd.chatmore.ui.adapter.ContactsAdapter
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 好友列表
 */
class ContactsViewModel : ViewModel() {
    private val mAdapter by lazy { ContactsAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    /**
     * 好友列表
     */
    fun netContactsList() {
        mDataRepository.getAllMyFriends()
                .subscribe({
                    EventBus.getDefault().post(ContactsVM(it, it.errorCode == 200))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    } else {
                        EventBus.getDefault().post(MessageFragmentViewModel.RefreshPeopleNumVM(it.data?.friendsList?.size
                                ?: 0))
                    }
                }, {
                    EventBus.getDefault().post(ContactsVM(isSuccess = false))
                })
    }

    fun netApplyFriendList() {
        val subscribe = mDataRepository.getApplyFriendList().subscribe(
                {
                    EventBus.getDefault().post(ApplyViewModel.ApplyListVM(it.errorCode == 200, it.data))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                },
                { EventBus.getDefault().post(ApplyViewModel.ApplyListVM(false)) })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 保存我的好友
     */
    fun saveContactsList(result: ContactsResult) {
        mDataRepository.saveContactsList(result)
        EventBus.getDefault().post(MessageFragmentViewModel.RefreshPeopleNumVM(result?.data?.friendsList?.size
                ?: 0))
    }

    class ContactsVM(val obj: ContactsResult? = null, val isSuccess: Boolean = false)
}