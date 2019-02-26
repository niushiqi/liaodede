package com.dyyj.idd.chatmore.model.mqtt.result


/**
 * 等级提升奖励
 */



data class ThreeDaySummaryResult(
    val completeTask: String,
    val rewardSum: RewardSum
)

data class RewardSum(
    val rewardCash: Double,
    val rewardCoin: Double,
    val rewardStone: Double
)