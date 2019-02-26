package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.TopicCommentResult
import com.dyyj.idd.chatmore.ui.adapter.MyCircleMsgAdapter
import org.greenrobot.eventbus.EventBus

class MessageCircleViewModel : ViewModel() {

    private val mAdapter by lazy { MyCircleMsgAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    /**
     * 获取消息列表
     */
    fun netCircleList(userId: String, page: Int, pageSize: Int) {
        val subscribe = mDataRepository.getTopicComment(userId).subscribe(
                {
                    EventBus.getDefault().post(MyCircleMsgVM(it.errorCode == 200, page != 1, it.errorMsg, it.data?.list))
                },
                {
                    EventBus.getDefault().post(MyCircleMsgVM(false, page != 1, "error"))
                }
        )
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 重置未读消息
     */
    fun netUpdateMsg() {
        val subscribe = mDataRepository.getUnUserMessage(mDataRepository.getUserid()!!).subscribe(
                { EventBus.getDefault().post(ClearUnMsgVM(it.errorCode == 200)) },
                { EventBus.getDefault().post(ClearUnMsgVM(false)) }
        )
        mCompositeDisposable.add(subscribe)
    }

    class ClearUnMsgVM(val success: Boolean)

    class MyCircleMsgVM(val success: Boolean, val more: Boolean, val message: String? = "", val vm: List<TopicCommentResult.ReplyComment>? = null)

}
