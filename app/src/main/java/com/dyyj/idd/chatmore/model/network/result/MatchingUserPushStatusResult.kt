package com.dyyj.idd.chatmore.model.network.result

data class MatchingUserPushStatusResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val pushResult: String? = ""
    )
}