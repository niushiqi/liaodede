package com.dyyj.idd.chatmore.model.network.result

data class TopicCommentResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val list: List<ReplyComment>? = listOf()
    )

    data class ReplyComment(
            val avatar: String? = "",
            val Id: String? = "",
            val topicId: String? = "",
            val replyMessage: String? = "",
            val replyUserId: String? = "",
            val replyVirtualName: String? = "",
            val replyIsRead: String? = "",
            val addTimeStamp: String? = "",
            val replyType: String? = "",
            val topicImgs: String = "",
            val topicType: String? = "",
            val topicBody: String? = ""
    )
}