package com.dyyj.idd.chatmore.model.mqtt.result

//"gameId":xx,"userId":"xxx","userName":"xxx","requestTime":"xxx"
//userId:请求方(用户A)id
//userName:请求方(用户A)名称
//requestTime:请求时间
data class Game2RequestResult(
        val gameId: String? = "",
        val userId: String? = "",
        val userName: String? = "",
        val requestTime: String? = ""
)