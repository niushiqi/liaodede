package com.dyyj.idd.chatmore.model.network.result



data class CoinSummaryResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: Data? = Data()
) {
    data class Data(
        val coin: String? = "",
        val totalGetCoin: Int? = 0,
        val todayGetCoin: Int? = 0,
        var coinHistory: List<CoinHistory>? = listOf()
    ) {
        data class CoinHistory(
            val consumeValue: String? = "",
            val consumeMark: String? = "",
            val consumeTimestamp: String? = ""
        )
    }
}