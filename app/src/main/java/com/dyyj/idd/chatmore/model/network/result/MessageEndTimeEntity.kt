package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by raul on 2018/12/26.
 */
data class MessageEndTimeEntity(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(val endTimestamp: Long? = 0)
}