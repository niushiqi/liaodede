package com.dyyj.idd.chatmore.model.network.result

data class MyGiftsResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: List<Gift>? = listOf()
){
    data class Gift(
            val id: String? = "",
            val name: String? = "",//礼物名称
            val icon: String? = "",//礼物图片
            val num: Int?,
            val coin: String? = "",
            var selected: Int? = 0
    )
}