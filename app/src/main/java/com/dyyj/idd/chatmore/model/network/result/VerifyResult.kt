package com.dyyj.idd.chatmore.model.network.result

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/12
 * desc   :
 */

data class VerifyResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: Data? = Data()
) {
    data class Data(
        val userId: String? = "",
        var username: String? = "",
        val mobile: String? = "",
        val lastLoginTime: String? = ""
    )
}