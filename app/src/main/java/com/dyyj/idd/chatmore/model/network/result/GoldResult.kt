package com.dyyj.idd.chatmore.model.network.result

data class GoldResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val exchangeCoin: String? = "",
            val exchangeCash: Float = 0.0f
    )
}