package com.dyyj.idd.chatmore.model.mqtt.result

//"gameId":xx,"userId":"xxx","userName":"xxx"
//userId:对方(用户B)id
//userName:对方(用户B)名称
data class Game2RejectResult(
        val gameId: Int? = 0,
        val userId: String? = "",
        val userName: String? = ""
)