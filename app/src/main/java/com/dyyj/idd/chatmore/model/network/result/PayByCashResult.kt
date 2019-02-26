package com.dyyj.idd.chatmore.model.network.result



data class PayByCashResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: Data? = Data()
) {
    data class Data(
        val orderBizNo: String? = "",
        val orderStatus: String? = ""
    )
}