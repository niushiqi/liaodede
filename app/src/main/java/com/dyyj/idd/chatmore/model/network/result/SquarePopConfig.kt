package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2019/1/3.
 */
data class SquarePopConfig(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(
            val isNeedDialog: Int? = 0,
            val popupNum: Int? = 0,
            val backgroundImg: String? = "",
            val target: String? = ""
    )
}