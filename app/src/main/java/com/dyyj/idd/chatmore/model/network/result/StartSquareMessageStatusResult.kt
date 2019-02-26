package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2018/12/31.
 */
data class StartSquareMessageStatusResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(
            val userId: String? = "",
            val messageUserId: String? = "",
            val startTimestamp: String? = "",
            val endTimestamp: String? = "",
            val finishTimestamp: String? = ""
    )
}