package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2018/12/27.
 */
data class UserSquareCommentResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: UserComment = UserComment()
) {

    data class UserComment(
            val squareTopicCommentId: String? = "",
            val squareTopicId: String? = "",
            val commentContent: String? = "",
            val commentImage: List<String>? = listOf(),
            val commentAgreeNum: String? = "",
            val commentReplyNum: String? = "",
            val commentTimestamp: String? = "",
            val squareTopicTitle: String? = "",
            val squareTopicContent: String? = "",
            val squareTopicImage: String? = "",
            val userId: String? = "",
            val nickname: String? = "",
            val avatar: String? = "",
            val userLevel: String? = "",
            val commentUserId: String? = "",
            val commentDisplay: String? = "",
            val gender: String? = "",
            val agreeAvatar: List<String>? = listOf(),
            val birthday: String? = ""


    )
}