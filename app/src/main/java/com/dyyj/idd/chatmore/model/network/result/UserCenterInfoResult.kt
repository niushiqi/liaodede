package com.dyyj.idd.chatmore.model.network.result

data class UserCenterInfoResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val userBaseInfo: UserBaseInfo = UserBaseInfo()
    )

    data class UserBaseInfo(
            val userId: String = "",
            val nickname: String = "",
            val gender: String = "",
            val userLevel: String = "",
            val isVip: String = "",
            val birthday: String = "",
            val age: String = "",
            val avatar: String = "",
            val nextLevelExperience: Int = 0,
            val userExperience:Int = 0
    )
}