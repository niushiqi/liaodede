package com.dyyj.idd.chatmore.model.network.result


class MainResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val user: User? = User(),
            val userBaseInfo: UserBaseInfo? = UserBaseInfo(),
            val userWallet: UserWallet? = UserWallet(),
            val matching: Matching? = Matching()
    ) {
        data class UserBaseInfo(//UserDetailInfoResult.DetailUserBaseInfo
                var nickname: String? = "",
                var gender: Int? = 1,
                var birthday: String? = "",
                var avatar: String? = "",
                var userLevel: String? = "",
                var userExperience: Int? = 0,
                var nextLevelExperience: Int? = 1
        )

        data class User(//UserDetailInfoResult.DetailUser
                val userId: String? = "",
                val mobile: String? = "",
                val registerTime: String? = "",
                val lastLoginTime: String? = "",
                val firstLogin: Int = 0
        )

        data class UserWallet(
                val userCoin: String? = "",
                val userCoinFreeze: String? = "",
                val userStone: String? = "",
                val userStoneFreeze: String? = "",
                val userCash: String? = "",
                val userCashFreeze: String? = "",
                val freezeCoin: String? = "",
                val envelopeTimes: String? = ""
        )

        data class Matching(
                val startTime: Long? = 0,
                val endTime: Long? = 0
        )
    }
}