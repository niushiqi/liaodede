package com.dyyj.idd.chatmore.model.network.result

data class Exchange2CoinResult(
        var errorCode: Int? = 0,
        var errorMsg: String? = "",
        var data: Data? = Data()
) {

  data class Data(var remainNum: Int? = 0)
}