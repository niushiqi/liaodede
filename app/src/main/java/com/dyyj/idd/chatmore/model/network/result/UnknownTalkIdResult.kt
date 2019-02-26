package com.dyyj.idd.chatmore.model.network.result


data class UnknownTalkIdResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = Data()) {

  data class Data(var talkId: String? = "")
}