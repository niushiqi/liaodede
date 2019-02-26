package com.dyyj.idd.chatmore.model.network.result

data class ShareMessageResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            var iconUrl: String? = "",
            var inviteUrl: String? = "",
            var title: String?= "",
            var inviteCode: String? = ""
    )
}