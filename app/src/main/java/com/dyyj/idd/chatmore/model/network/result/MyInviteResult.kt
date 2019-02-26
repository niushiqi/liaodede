package com.dyyj.idd.chatmore.model.network.result

data class MyInviteResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val inviteCode: String? = "",
            val usedTime: String? = "",
            val limitTime: String? = ""
    )
}