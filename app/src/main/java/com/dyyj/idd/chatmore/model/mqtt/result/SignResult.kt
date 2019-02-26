package com.dyyj.idd.chatmore.model.mqtt.result

data class SignResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val tags: List<Sign>? = listOf()
    ) {
        data class Sign(
                val tagName: String? = ""
        )
    }
}