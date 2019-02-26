package com.dyyj.idd.chatmore.model.mqtt.result

//“gameId”:xx,”useraId”:”10130,xxx”,”userbId”:”10131,xxx”
//useraId:用户a id
//userbId:用户b id
data class Game2ResponseResult(
        val gameId: Int? = 0,
        val useraId: String? = "",
        val userbId: String? = ""
)