package com.dyyj.idd.chatmore.model.mqtt.result

// 领取礼物
data class NoticeTaskRewardResult(
        var rewardTitle: String? = "",
        var taskId: String? = "",
        var taskType: Int? = 0,
        var rewardCoin: Double? = 0.0,
        var rewardStone: Double? = 0.0,
        var rewardCash: Double? = 0.0
)