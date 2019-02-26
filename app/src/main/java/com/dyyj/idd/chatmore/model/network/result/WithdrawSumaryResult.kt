package com.dyyj.idd.chatmore.model.network.result


data class WithdrawSumaryResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: Data? = Data()
) {
    data class Data(
        val alipayAccount: String? = "",
        val alipayRealName: String? = "",
        val tip: String? = "",
        val balance: Double = 0.0,
        val withdrawUnit: Array<String> ?= arrayOf()
    )
}