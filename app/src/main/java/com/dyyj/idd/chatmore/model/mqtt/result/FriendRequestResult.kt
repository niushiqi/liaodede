package com.dyyj.idd.chatmore.model.mqtt.result

data class FriendRequestResult(
        var requestId: String? = "",
        val userId: String? = "",
        val nickname: String? = "",
        val avatar: String? = ""
)