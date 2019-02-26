package com.dyyj.idd.chatmore.model.mqtt.result

/**
 * 新手任务
 */
data class NewbieTaskResult2(
        val rewardType: String? = "",
        val rewardCash: Double? = 0.0,
        val rewardStone: Double? = 0.0,
        val rewardCoin : Double? = 0.0,
        val rewardId: Int = 0,
        var title: String? = "",
        var autoClose: Boolean = true
)