package com.dyyj.idd.chatmore.model.mqtt.result


/**
 * 匹配失败
 */
data class MatchingFailedResult(
    val userId: String? = "",
    val messageType: String? = ""
)