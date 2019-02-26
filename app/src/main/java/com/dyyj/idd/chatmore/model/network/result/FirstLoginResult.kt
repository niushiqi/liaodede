package com.dyyj.idd.chatmore.model.network.result



data class FirstLoginResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: Data? = Data()
) {
    data class Data(
        val rewardId: String? = "",
        val rewardUserId: String? = "",
        val rewardType: String? = "",
        val rewardCoin: String? = "",
        val rewardStone: String? = "",
        val rewardCash: String? = "",
        val rewardTime: String? = "",
        val rewardReceiveTime: String? = "",
        val rewardExpireTime: String? = ""
    )
}