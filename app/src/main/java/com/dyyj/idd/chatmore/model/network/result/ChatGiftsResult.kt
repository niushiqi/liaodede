package com.dyyj.idd.chatmore.model.network.result

data class ChatGiftsResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val ownDedouNum:String? = "",
            val giftList:List<Gift>? = listOf()
    ){
        data class Gift(
                val id:String? = "",
                val name:String? = "",
                val icon:String? = "",
                val dedouNum:Int? = 0
        )
    }


}