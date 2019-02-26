package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2019/1/3.
 */
data class SquareUnReadMessageNumResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(
            val unReadMessageNum: String? = "",
            val lastMessageAvatar: String? = ""
    )
}