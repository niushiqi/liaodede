package com.dyyj.idd.chatmore.model.network.result

data class MainMsgResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val count: Int? = 0,
            val has_message: Int? = 0
    )
}