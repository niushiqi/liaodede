package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.UserMessageResult
import com.dyyj.idd.chatmore.model.network.result.UserTopicResult
import com.dyyj.idd.chatmore.ui.adapter.MyCircleAdapter
import org.greenrobot.eventbus.EventBus

class MyCircleViewModel : ViewModel() {
    private val mAdapter by lazy { MyCircleAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    fun netCircleList(userId: String, page: Int, pageSize: Int) {
        val subscribe = mDataRepository.getMyTopicList(userId, page.toString(), pageSize.toString()).subscribe(
                {
                    EventBus.getDefault().post(MyCircleListVM(it.errorCode == 200, page != 1, it.errorMsg, it.data?.list))
                },
                {
                    EventBus.getDefault().post(MyCircleListVM(false, page != 1, "error"))
                }
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netCommentSend(userId: String, replyMessage: String, topicId: String) {
        val subscribe = mDataRepository.commentTopic(userId, replyMessage, topicId).subscribe(
                { EventBus.getDefault().post(MyRefreshComment(it.errorCode == 200)) },
                { EventBus.getDefault().post(MyRefreshComment(false)) }
        )
        mCompositeDisposable.add(subscribe)
    }

    fun netNewMsg() {
        val subscribe = mDataRepository.getUserMessage(mDataRepository.getUserid()!!).subscribe ({
            EventBus.getDefault().post(MyUnReadMsgVM(it.errorCode == 200, it))
        },{})
        mCompositeDisposable.add(subscribe)
    }

    class MyUnReadMsgVM(val success: Boolean, val obj: UserMessageResult)

    class MyCircleListVM(val success: Boolean, val more: Boolean, val message: String? = "", val vm: List<UserTopicResult.UserTopic>? = null)

    class MyRefreshComment(val success: Boolean)

    class MyCommentSendVM(val topicId: String, val userID:String)

}