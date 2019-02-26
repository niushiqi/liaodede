package com.dyyj.idd.chatmore.model.mqtt.result


/**
 * 等级提升奖励
 */
data class LevelUpgradeResult(
        val level: String? = "",
        val message: Msg? = Msg()
) {
    data class Msg(
            val line1: String? = "",
            val line2: String? = "",
            val line3: String? = ""
    )
}

