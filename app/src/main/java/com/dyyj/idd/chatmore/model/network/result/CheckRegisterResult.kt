package com.dyyj.idd.chatmore.model.network.result

data class CheckRegisterResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val rs: Int? = 0
    )
}