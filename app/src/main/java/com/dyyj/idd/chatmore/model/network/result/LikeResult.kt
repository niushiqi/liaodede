package com.dyyj.idd.chatmore.model.network.result

data class LikeResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val replyVirtualName: String? = ""
)