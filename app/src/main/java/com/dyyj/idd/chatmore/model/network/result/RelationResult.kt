package com.dyyj.idd.chatmore.model.network.result

data class RelationResult(
        val errorCode: Int?= 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val isFriend:String? = "0"//1好友 0非好友
    )
}