package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2018/12/26.
 */
data class UserSquareDynamicListResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: List<UserMessage>? = listOf()
) {
    data class UserMessage(
            val id: String? = "",
            val dynamicType: String? = "",
            val squareTopicId: String? = "",
            val squareTopicTitle: String? = "",
            val squareTopicContent: String? = "",
            val squareTopicImage: String? = "",
            val squareTopicCommentId: String? = "",
            val squareTopicCommentContent: String? = "",
            val squareTopicCommentImage: List<String>? = listOf(),
            val squareTopicCommentReplyId: String? = "",
            val squareTopicCommentReply: String? = "",
            val squareTopicCommentReplyImage: List<String>? = listOf(),
            val commentId: String? = "",
            val commentContent: String? = "",
            val commentImage: List<String>? = listOf(),
            val commentAgreeNum: String? = "",
            val replyId: String? = "",
            val replyContent: String? = "",
            val replyImage: List<String>? = listOf(),
            val squareUserId: String? = "",
            val nickname: String? = "",
            val avatar: String? = "",
            val dynamicTimestamp: String? = ""
    )
}