package com.dyyj.idd.chatmore.model.mqtt.result


/**
 *  挂断后获取奖励
 */

data class HangUpResult(
    val rewardType: String? = "",
    val rewardId: String? = "",
    val rewardCoin: Double? = 0.0,
    val rewardStone: Double? = 0.0,
    val rewardCash: Double? = 0.0
)