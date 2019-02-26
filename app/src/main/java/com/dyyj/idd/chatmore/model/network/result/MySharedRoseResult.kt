package com.dyyj.idd.chatmore.model.network.result

data class MySharedRoseResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(
        val receive: List<Gift>? = listOf(),
        val giveout: List<Gift>? = listOf()
    ){
        data class Gift(
             val id: String? = "",
             val gift_id: String? = "",
             val gift_name: String? = "",//礼物名称
             val gift_icon: String? = "",//礼物图片
             val talk_id: String? = "",
             val sender_id: String? = "",//赠送者uid
             val sender_name: String? = "",//赠送者名称
             val sender_avatar: String? = "",//赠送者头像
             val receiver_id: String? = "", //接收者uid
             val receiver_name: String? = "",//接收者名称
             val receiver_avatar: String? = "",//接收者头像
             val ctime: String? = "",//赠送时间
             val expired_time: String? = "",
             val status: String? = "",
             var isSelected: Int? = 0
        )
    }
}