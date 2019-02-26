package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.eventtracking.EventGiftMessage
import com.dyyj.idd.chatmore.model.network.result.ChatGiftsResult
import com.dyyj.idd.chatmore.model.network.result.SendGiftResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/17
 * desc   : 礼物列表
 */
class SendGiftInChatViewModel : ViewModel() {

    //获取礼物列表
    fun getChatGiftsList() {
        val subscribe = mDataRepository.getChatGifts().subscribe(
                {
                    if (it.errorCode == 200 && it.data!!.giftList!!.isNotEmpty()) {
                        EventBus.getDefault().post(ChatGiftsDataVM(true, it))
                    } else {
                        niceToast(it.errorMsg)
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    //调用送礼物的接口
    fun sendGift(toUserId: String, giftId: String) {
        val subscribe = mDataRepository.sendGift(toUserId, giftId).subscribe(
                {
                    if (it.errorCode == 200) {
                        niceToast("礼物已送出~")
                        EventBus.getDefault().post(SendGiftVM(true, it))
                        EventBus.getDefault().post(ChatViewModel.SendGiftCardMessage(EventGiftMessage(
                                giftId, it.data?.giftName,
                                it.data?.giftIcon,
                                "0", mDataRepository.getUserid(), "德玛西亚",
                                toUserId, "德邦总管")))
                    } else {
                        if ("得得豆不足".contains(it.errorMsg!!)) {
                            EventBus.getDefault().post(GoToBuyProudPeasPageVM(true))
                        }
                        niceToast(it.errorMsg)
                    }
                },
                { niceToast(it.message) }
        )
        mCompositeDisposable.add(subscribe)
    }


    class ChatGiftsDataVM(val success: Boolean, val obj: ChatGiftsResult? = null)

    class GoToBuyProudPeasPageVM(val success: Boolean)

    class SendGiftVM(val success: Boolean, val obj: SendGiftResult? = null)

}