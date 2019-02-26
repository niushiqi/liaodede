package com.dyyj.idd.chatmore.model.network.result

data class ApplyFriendResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val requestList: List<ApplyFriend>? = listOf()
    )

    data class ApplyFriend(
            val requestId: String? = "",
            val requestUserId: String? = "",
            val toUserId: String? = "",
            val requestMessage: String? = "",
            val requestTimestamp: String? = "",
            var handleResult: String? = "",
            val handleMessage: String? = "",
            val handleTimestamp: String? = "",
            val nickname: String? = "",
            val gender: String? = "",
            val userLevel: String? = "",
            val age: Int? = 0,
            val birthday: String? = "",
            val avatar: String? = "",

            var isNew: Boolean? = false
    )
}