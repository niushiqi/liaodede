package com.dyyj.idd.chatmore.model.mqtt.result


/**
 * 首次聊天奖励
 */

data class FirstChatResult(
    val rewardType: String? = "",
    val rewardId: String? = "",
    val rewardCoin: String? = "",
    val rewardStone: String? = "",
    val rewardCash: String? = "",
    var title: String? = ""
)