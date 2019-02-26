package com.dyyj.idd.chatmore.model.network.result

data class SendGiftResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(
            val giftName: String? = "",
            val giftIcon: String? = "",
            val ownDedouNum: String? = ""
    )
}