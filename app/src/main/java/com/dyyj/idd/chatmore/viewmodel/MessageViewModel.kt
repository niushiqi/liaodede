package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.database.entity.MessageEntity
import com.dyyj.idd.chatmore.ui.adapter.MessageAdapter
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   :
 */
class MessageViewModel : ViewModel() {

  private val mAdapter by lazy { MessageAdapter() }

  /**
   * 获取Adapter
   */
  fun getAdapter() = mAdapter

    /**
     * 加载消息列表
     */
  fun loadConversationList(isRefresh: Boolean) {


    val subscribe = mDataRepository.queryLastConversationAllByFriend().subscribe({
                                                                                   if (isRefresh) {
                                                                                     EventBus.getDefault().post(
                                                                                         RefreshVM(
                                                                                             it))
                                                                                   } else {
                                                                                     EventBus.getDefault().post(
                                                                                         MessageVM(
                                                                                             it))
                                                                                   }
                                                                                 }, {
                                                                                   Timber.tag(
                                                                                       "chat").e(
                                                                                       it.message)
                                                                                 })
    mCompositeDisposable.add(subscribe)
  }

    fun getApplyNum() {
        val subscribe = mDataRepository.getUnHandleFriendRequest()
                .subscribe({
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    } else {
                        EventBus.getDefault().post(EventMessage(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT,EventConstant.WHAT.WHAT_UNREAD_FRIEND_COUNT,it.data.size))
                    }
                }, { })
        mCompositeDisposable.add(subscribe)
    }

  /**
   * 订阅未读消息
   */
  fun queryUnreadMessageList() {
    val subscribe = mDataRepository.queryUnreadMessageAll().subscribe({
                                                                        loadConversationList(false)
                                                                      }, {
                                                                        Timber.tag("chat").e(
                                                                            it.message)
                                                                      })
    mCompositeDisposable.add(subscribe)
  }

  fun getNewMessage() = mDataRepository.getSystemMessage() > 0

  /**
   * 清空系统消息
   */
  fun celarNewMessage() {
    mDataRepository.clearSystemMessage()
  }

  class MessageVM(val obj: List<MessageEntity>)
  class RefreshVM(val obj: List<MessageEntity>)
}