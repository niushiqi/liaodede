package com.dyyj.idd.chatmore.model.mqtt.result

// 领取礼物
data class NoticeLevelUpResult(
        var level: Int? = 0,
        val message: Msg? = Msg()
) {
    data class Msg(
            val line1: String? = "",
            val line2: String? = "",
            val line3: String? = ""
    )
}