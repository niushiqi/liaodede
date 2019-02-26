package com.dyyj.idd.chatmore.model.network.result




data class WithdrawCheckResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(

            val tip: String? = "",
            val reason: String? = "",
            val isOk: Int = 0
    )
}