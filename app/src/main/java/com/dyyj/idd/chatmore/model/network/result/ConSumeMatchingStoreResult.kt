package com.dyyj.idd.chatmore.model.network.result



data class ConSumeMatchingStoreResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: Data? = Data()
) {
    data class Data(
        val consumeId: Int? = 0
    )
}