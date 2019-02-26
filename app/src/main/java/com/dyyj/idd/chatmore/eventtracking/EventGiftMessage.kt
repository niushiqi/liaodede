package com.dyyj.idd.chatmore.eventtracking

/**
 * Created by wangbin on 2019/1/20.
 * 描述礼物卡相关的信息
 */
data class EventGiftMessage(
        val giftId: String? = "0",
        val giftName: String? = "礼物",
        val giftIcon: String? = "",
        val giftPrice: String? = "0",
        val fromUserId: String? = "",
        val fromUserName: String? = "",
        val toUserId: String? = "",
        val toUserName: String? = ""
)