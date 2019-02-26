package com.dyyj.idd.chatmore.model.network.result

data class CustomCoinResult(var errorCode: Int?, var errorMsg: String?, var data: Data?) {
    data class Data(var freezeCoin: String? = "0", var rewardCoin: String? = "0")
}