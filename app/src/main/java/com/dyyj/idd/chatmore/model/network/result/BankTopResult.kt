package com.dyyj.idd.chatmore.model.network.result

data class BankTopResult(
        val errorCode:Int? = 0,
        val errorMsg:String? = "",
        val list: List<BankTopData>? = listOf(),
        val userInfo: BankUserInfo? = BankUserInfo()
) {
    data class BankTopData(
            val rank: String? = "",
            val userId: String? = "",
            val nickName: String? = "",
            val avatar: String? = "",
            val bankCoin: String? = "",
            val bankLevel: String? = "",
            val friendsNum: Int? = 0
    )

    data class BankUserInfo(
            val rank: String? = "",
            val userId: String? = "",
            val nickName: String? = "",
            val avatar: String? = "",
            val bankCoin: String? = "",
            val bankLevel: String? = "",
            val friendsNum: Int? = 0
    )
}
