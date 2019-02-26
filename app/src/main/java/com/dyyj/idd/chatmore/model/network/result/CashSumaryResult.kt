package com.dyyj.idd.chatmore.model.network.result



data class CashSumaryResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: Data? = Data()
) {
    data class Data(
        val socialTip: String? = "",
        val inviteTip: String? = "",
        val socialNum: Double = 0.0,
        val inviteNum: Double = 0.0
    )
}