package com.dyyj.idd.chatmore.model.network.result

data class LoginResult(
        val errorCode: Int?= 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            var username: String? = "",
            val userId: String? = "",
            val mobile: String? = "",
            val lastLoginTime: String? = "",
            val firstLogin: String? = "",
            val token: String? = "",
            val serverInfo: ServerInfo? = ServerInfo()
    ) {
        data class ServerInfo(
                val talk: Talk? = Talk(),
                val chat: Chat? = Chat(),
                val subscribe: SubscribeData? = SubscribeData()
        ) {
            data class Talk(
                    val host: String? = "",
                    val port: String? = "",
                    val account: String? = ""
            )
            data class Chat(
                    val host: String? = "",
                    val port: String? = "",
                    val account: String? = "",
                    val node: String? = ""
            )
            data class SubscribeData(
                    val host: String? = "",
                    val port: String? = "",
                    val topic: String? = ""
            )
        }
    }
}