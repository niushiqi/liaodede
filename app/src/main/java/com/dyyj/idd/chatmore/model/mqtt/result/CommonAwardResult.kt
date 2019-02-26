package com.dyyj.idd.chatmore.model.mqtt.result


/**
 * 固定奖励
 */

data class CommonAwardResult(
    val rewardType: String? = "",
    val rewardId: String? = "",
    val rewardCoin: Double? = 0.0,
    val rewardStone: Double? = 0.0,
    val rewardCash: Double? = 0.0
)