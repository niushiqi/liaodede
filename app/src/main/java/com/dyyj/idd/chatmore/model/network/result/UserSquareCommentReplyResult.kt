package com.dyyj.idd.chatmore.model.network.result

import android.os.Build
import android.support.annotation.RequiresApi
import java.util.*

/**
 * Created by wangbin on 2018/12/27.
 */
data class UserSquareCommentReplyResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: List<UserReply>? = listOf()
) {
    data class UserReply(
            val squareTopicCommentReplyId: String? = "",
            val squareTopicId: String? = "",
            val squareTopicCommentId: String? = "",
            val replyUserId: String? = "",
            val replyToReplyId: String? = "",
            val replyToUserId: String? = "",
            val replyContent: String? = "",
            val replyImage: List<String>? = listOf(),
            val replyDisplay: String? = "",
            var replyAgreeNum: String? = "",
            val replyReplyNum: String? = "",
            var replyNb: String? = "",
            val replyTimestamp: String? = "",
            val replyUserNickname: String? = "",
            val replyToUserNickname: String? = "",
            val avatar: String? = "",
            val floor: String? = ""
    ) {
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun hashCode(): Int {
            return Objects.hash(squareTopicCommentReplyId, squareTopicId, squareTopicCommentId,
                    replyUserId, replyToReplyId, replyToUserId, replyContent, replyImage,
                    replyDisplay, replyAgreeNum, replyReplyNum, replyNb, replyTimestamp, replyUserNickname,
                    replyToUserNickname, floor)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is UserReply) return false
            return this.squareTopicCommentReplyId == other.squareTopicCommentReplyId
                    && this.squareTopicId == other.squareTopicId
                    && this.squareTopicCommentId == other.squareTopicCommentId
                    && this.replyUserId == other.replyUserId
                    && this.replyToReplyId == other.replyToReplyId
                    && this.replyToUserId == other.replyToUserId
                    && this.replyContent == other.replyContent
                    && this.replyImage == other.replyImage
                    && this.replyDisplay == other.replyDisplay
                    && this.replyAgreeNum == other.replyAgreeNum
                    && this.replyReplyNum == other.replyReplyNum
                    && this.replyNb == other.replyNb
                    && this.replyTimestamp == other.replyTimestamp
                    && this.replyUserNickname == other.replyUserNickname
                    && this.replyToUserNickname == other.replyToUserNickname
                    && this.floor == other.floor
        }
    }
}