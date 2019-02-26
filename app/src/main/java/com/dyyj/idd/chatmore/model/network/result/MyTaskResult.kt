package com.dyyj.idd.chatmore.model.network.result

data class MyTaskResult(
        var errorCode: Int? = 0, var errorMsg: String? = "",
        var data: Data? = Data()
) {
    data class Data(
//            var tip: String? = "",
            var myTasks: List<MyTask>? = listOf()
//            var cardInfo: CardInfo? = CardInfo()
    ) {
        data class MyTask(
                var invitedUsername: String? = "",
                var invitedMobile: String? = "",
                var rewardNum: String? = "",
                var totalRewardNum: String? = "",
                var lastUpdateTimestamp: String? = "",
                var status: String? = ""
        )
//        data class CardInfo(
//                var iconUrl: String? = "",
//                var inviteUrl: String? = "",
//                var title: List<String>? = listOf()
//        )
    }
}