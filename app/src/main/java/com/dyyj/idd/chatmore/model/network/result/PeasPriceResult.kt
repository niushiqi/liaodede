package com.dyyj.idd.chatmore.model.network.result

data class PeasPriceResult(
        var errorCode: Int? = 0, var errorMsg: String? = "",
        var data: Data? = Data()
) {
    data class Data(
//            var tip: String? = "",
            var myTasks: List<PeasPrice>? = listOf()
//            var cardInfo: CardInfo? = CardInfo()
    ) {
        data class PeasPrice(
                var PeasNums: Int,
                var PeaPrice: Int
                /*var invitedUsername: String? = "",
                var invitedMobile: String? = "",
                var rewardNum: String? = "",
                var totalRewardNum: String? = "",
                var lastUpdateTimestamp: String? = "",
                var status: String? = ""*/
        )
    }
}