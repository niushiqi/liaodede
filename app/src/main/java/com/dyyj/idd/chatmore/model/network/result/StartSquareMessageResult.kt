package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2018/12/30.
 */

data class StartSquareMessageResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(val endTimestamp: String? = "")
}