package com.dyyj.idd.chatmore.model.network.result


data class Conver2CoinResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = Data()) {

  data class Data(var remainNum: Int? = 0)
}