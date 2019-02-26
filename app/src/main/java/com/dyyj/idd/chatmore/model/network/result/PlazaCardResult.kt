package com.dyyj.idd.chatmore.model.network.result

/**
 * 帖子列表
 */
data class PlazaCardResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        var data: List<PlazaTopic> = listOf()
) {
    data class PlazaTopic(
            var squareTopicCommentId: String = "",
            val dynamicId: String = "",
            var squareTopicId: String = "",
            var commentContent: String? = "",
            var commentImage: List<String>? = listOf(),
            var commentAgreeNum: Int = 0,
            var commentIsHot: Int = 0,
            var commentReplyNum: Int = 0,
            var commentTimestamp: String? = "",
            var squareTopicTitle: String? = "",
            var squareTopicContent: String? = "",
            var squareTopicImage: String? = "",
            var userId: String ?= "",
            var nickname: String? = "",
            var avatar: String? = "",
            //
            var userLevel: String? = "",
            var agree: Int? = 0,
            var follow: Int? = 0

    )

}