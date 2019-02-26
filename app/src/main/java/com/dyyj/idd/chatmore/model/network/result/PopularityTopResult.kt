package com.dyyj.idd.chatmore.model.network.result

data class PopularityTopResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: List<PopularityTopData>? = listOf(),
        val userInfo: PopularityUserInfo? = PopularityUserInfo()
) {
    data class PopularityTopData(
            val rank:String? = "",
            val userId:String? = "",
            val nickName:String? = "",
            val avatar:String? = "",
            val friendsLevel:String? = "",
            val friendsNum:String? = ""
    )

    data class PopularityUserInfo(
//            "userInfo":{"rank":"90","userId":"10292","nickName":"\u5662\u5662\u5662\u54e6\u54e6","avatar":"http:\/\/api.ddaylove.com\/avatar\/20181009124312_8787.jpg","friendsNum":"1"}
            val rank:String? = "",
            val userId:String? = "",
            val nickName:String? = "",
            val avatar:String? = "",
            val friendsNum:String? = ""
    )
}