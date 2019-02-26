package com.dyyj.idd.chatmore.model.network.result

data class Game2RequestResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val gameId: Int? = 0
    )
}