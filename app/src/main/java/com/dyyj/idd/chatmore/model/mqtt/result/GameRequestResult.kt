package com.dyyj.idd.chatmore.model.mqtt.result

data class GameRequestResult(
//"gameId":xx,"userId":"xxx","userName":"xxx","requestTime":"xxx"
        var gameId: Int? = 0,
        var userId: String? = "",
        var userName: String? = "",
        var requestTime: String? = "",
        var expectRs: Int? = 0
)