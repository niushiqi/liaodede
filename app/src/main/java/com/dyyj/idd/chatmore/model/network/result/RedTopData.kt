package com.dyyj.idd.chatmore.model.network.result

data class RedTopData(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: List<Data>? = listOf(),
        val userInfo: RedUserInfo? = RedUserInfo()
) {
    data class Data(
//            "rank": "1",
//    "userId": "10129",
//    "nickName": "root",
//    "avatar": "http://api.ddaylove.com/avatar/20180915130754_8784.png",
//    "opendRedEnvelopeNum": "117",
//    "userCash": "0.00",
//    "userCoin": "3652"
              val rank: String? = "",
              val userId: String? = "",
              val nickName: String? = "",
              val avatar: String? = "",
              val opendRedEnvelopeNum: String? = "",
              val userCash: String? = "",
              val userCoin: String? = ""
    )

    data class RedUserInfo(
//            "userInfo":{"rank":"173","userId":"10292","nickName":"\u5662\u5662\u5662\u54e6\u54e6","avatar":"http:\/\/api.ddaylove.com\/avatar\/20181009124312_8787.jpg","opendRedEnvelopeNum":"","userCash":"0.04","userCoin":"474"}
            val rank: String? = "",
            val userId: String? = "",
            val nickName: String? = "",
            val avatar: String? = "",
            val opendRedEnvelopeNum: String? = "",
            val userCash: String? = "",
            val userCoin: String? = ""
    )
}
