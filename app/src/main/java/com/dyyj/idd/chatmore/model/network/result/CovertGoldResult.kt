package com.dyyj.idd.chatmore.model.network.result

data class CovertGoldResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val coin: String? = "",
            val exchangeRate: String? = "",
            val isFirstTimeChange: Int = 0,
            val exchangeCash: Double = 0.0,
            val maxExchangeCash: Double = 0.0
    )
}