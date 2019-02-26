package com.dyyj.idd.chatmore.model.mqtt.result

data class SignTaskResult(
        val rewardType: String? = "",
        val rewardCash: Double? = 0.0,
        val rewardStone: Double? = 0.0,
        val rewardCoin : Double? = 0.0,
        val rewardId: Int = 0,
        val message: Msg? = Msg()
) {
    data class Msg(
            val line1: String? = ""
    )
}