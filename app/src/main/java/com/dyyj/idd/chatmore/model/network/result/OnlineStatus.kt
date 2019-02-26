package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by Who are you~ on 2018/12/4.
 */
data class OnlineStatus(
        var errorCode: Int? = 0,
        var errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(
        var lastActivityTime: String? = "",
        var online: String? = ""
    )
}

