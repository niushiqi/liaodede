package com.dyyj.idd.chatmore.model.network.result

data class SignInStatusResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val signInTimes: Int = 0,
            val nextReward: NextReward? = null,
            val nextRewardAttract: NextAttract? = null,
            val signInBar: List<SignInData>? = arrayListOf()
    ) {
        data class NextReward(
                val rewardCoin: String? = "",
                val rewardStone: String? = ""
        )

        data class NextAttract(
                val nextRewardAttractRemainDays: Int = 0,
                val nextRewardAttractCoin: String? = ""
        )

        data class SignInData(
                val signInDays: Int = 0,
                val rewardCoin: String? = "",
                val attract: String? = ""
        )
    }
}