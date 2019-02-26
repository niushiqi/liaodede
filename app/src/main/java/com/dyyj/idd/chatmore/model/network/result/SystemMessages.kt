package com.dyyj.idd.chatmore.model.network.result

data class SystemMessages(
        var errorCode: Int? = 0, var errorMsg: String? = "",
        var data: List<Data>? = arrayListOf()
) {
    data class Data(
            var name: String? = "",
            var num: Int? = 0,
            var rewardNum: Int? = 0
//            var invitedUsername: String? = "",
//            var invitedMobile: String? = "",
//            var rewardNum: String? = "",
//            var totalRewardNum: String? = "",
//            var lastUpdateTimestamp: String? = ""
//            "name": "冰糖葫芦",
//    "num": 13,
//    "rewardNum": 26
    )
}