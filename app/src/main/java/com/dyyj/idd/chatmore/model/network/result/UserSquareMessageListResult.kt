package com.dyyj.idd.chatmore.model.network.result

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by wangbin on 2018/12/25.
 */
data class UserSquareMessageListResult(

        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: List<UserMessage>? = listOf()
) {
    /*data class Data(
            val list: List<UserMessage>? = listOf()
    )*/
    @Parcelize
    data class UserMessage(
            val squareTopicId: Int? = 0,
            val squareTopicTitle: String? = "",
            val squareTopicContent: String? = "",
            val squareTopicImage: String? = "",
            val squareCommentId: String? = "",
            val commentContent: String? = "",
            val commentImage: List<String>? = listOf(),
            val squareReplyId: String? = "",
            val replyContent: String? = "",
            val replyImage: List<String>? = listOf(),
            val userId: String? = "",
            val isOfficial: String? = "",
            val nickname: String? = "",
            var avatar: String? = "",
            val messageType: String? = "",
            val messageContent: String? = "",
            val messageTimestamp: String? = ""
    ) : Parcelable
}