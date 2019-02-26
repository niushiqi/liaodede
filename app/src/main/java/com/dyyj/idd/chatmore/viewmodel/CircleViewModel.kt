package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.UserMessageResult
import com.dyyj.idd.chatmore.model.network.result.UserTopicResult
import com.dyyj.idd.chatmore.ui.adapter.CircleAdapter
import org.greenrobot.eventbus.EventBus

class CircleViewModel: ViewModel() {
    private val mAdapter by lazy { CircleAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    fun netCircleList(userId: String, page: Int, pageSize: Int) {
        val subscribe = mDataRepository.getTopicList(userId, page.toString(), pageSize.toString()).subscribe(
                {
                    EventBus.getDefault().post(CircleListVM(it.errorCode == 200, page != 1, it.errorMsg, it.data?.list))
                },
                {
                    EventBus.getDefault().post(CircleListVM(false, page != 1, "error"))
                }
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netCommentSend(userId: String, replyMessage: String, topicId: String) {
        val subscribe = mDataRepository.commentTopic(userId, replyMessage, topicId).subscribe(
                { EventBus.getDefault().post(RefreshComment(it.errorCode == 200)) },
                { EventBus.getDefault().post(RefreshComment(false)) }
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netNewMsg() {
        val subscribe = mDataRepository.getUserMessage(mDataRepository.getUserid()!!).subscribe ({
            EventBus.getDefault().post(MyUnReadMsgVM(it.errorCode == 200, it))
        },{})
        mCompositeDisposable.add(subscribe)
    }

    fun netDeleteDynamics() {
        val subscribe = mDataRepository.deleteMainMsg().subscribe {
            //所有好友发布动态 统一置已读状态
            val tmpSubscribe = mDataRepository.getMainMsg().subscribe {
                EventBus.getDefault().post(OpenCallViewModel.DynamicsMsgVM(it.errorCode == 404, it.data))
            }
            mCompositeDisposable.add(tmpSubscribe)
        }
        mCompositeDisposable.add(subscribe)
    }

    class MyUnReadMsgVM(val success: Boolean, val obj: UserMessageResult)

    class CircleUnReadMsgVM(val success: Boolean, val obj: UserMessageResult)

    class CircleListVM(val success: Boolean, val more: Boolean, val message: String? = "", val vm: List<UserTopicResult.UserTopic>? = null)

    class RefreshComment(val success: Boolean)

    class CommentSendVM(val topicId: String , val userID:String)
}