package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.SystemMessageAdapter
import org.greenrobot.eventbus.EventBus
import com.hyphenate.chat.EMMessage
import timber.log.Timber

/**
 * Created by wangbin on 2018/12/9.
 */
class SystemMessageViewModel : ViewModel() {
    val userId by lazy { mDataRepository.getUserid() }
    private val mAdapter by lazy { SystemMessageAdapter() }

    fun getAdapter() = mAdapter

    /**
     * 加载系统消息列表
     */
    fun loadSystemMessageList() {
        val subscribe = mDataRepository.querySystemMessageList(userId!!).subscribe({ list ->
            EventBus.getDefault().post(SystemMessageViewModel.SystemMessageVM(list))
        }, {
            Timber.tag("niushiqi-sysmess").e("记录获取失败")
        }, {
            Timber.tag("niushiqi-sysmess").i("完成")
        })
        mCompositeDisposable.add(subscribe)

    }

    /**
     * 清空未读消息
     */
    fun clearUnreadMessage(friendUserId: String) {
        EventBus.getDefault().post(ClearUnreadMessage())
        mDataRepository.clearUnreadMessage(friendUserId)
    }

    class SystemMessageVM(val list: List<EMMessage>? = null)

    class ClearUnreadMessage()

    class ShowLargeImageVM(val imageUrl: String)

}