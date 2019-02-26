package com.dyyj.idd.chatmore.model.network.result

data class TaskRewardResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val rewardCoin: String? = "",
            val rewardCash: String? = "",
            val rewardStone: String? = ""

    )
}