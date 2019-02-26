package com.dyyj.idd.chatmore.model.mqtt.result

data class GameRefuseResult(
//"gameId":xx,"userId":"xxx","userName":"xxx","msg":"xxx"
        var gameId: Int? = 0,
        var userId: String? = "",
        var userName: String? = "",
        var msg: String? = ""
)