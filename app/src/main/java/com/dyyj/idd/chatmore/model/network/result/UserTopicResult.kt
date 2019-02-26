package com.dyyj.idd.chatmore.model.network.result

data class UserTopicResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val list: List<UserTopic>? = listOf()
    )

    data class UserTopic(
            val Id: String? = "",
            val userId: String? = "",
            val topicType: String? = "",
            val topicBody: String? = "",
            val topicImgs: List<String>? = listOf(),
            val topicReplyCount: String? = "",
            var topicVoteCount: String? = "",
            val topicTargetId: String? = "",
//            val topicExt: TopicExt? = TopicExt(),
            val topicExt: TopicExt? = TopicExt(),
            val addTimeStamp: String? = "",
            val userInfo: UserInfo? = UserInfo(),
            val userReplyList: List<UserReply>? = listOf(),
            var userVoteList: ArrayList<UserVote>? = arrayListOf(),
            val total: Int? = 0,
            var isVote: Int? = 0,
            var showNums: Int = 2
    )

    data class TopicExt(
            val userLevel: Int? = 0,

            val completeTaskNum: Int? = 0,
            val rewardCoin: Int? = 0,

            val bankCoin: Int? = 0,
            val bankLevel: Int? = 0,

            val msgList: List<UserExt>? = listOf()
    )

    data class UserExt(
            val user_id: String? = "",
            val rank: String? = "",
            val avatar: String? = ""
    )

    data class UserInfo(
            val userId: String? = "",
            val nickname: String? = "",
            val gender: String? = "",
            val userLevel: String? = "",
            val isVip: String? = "",
            val birthday: String? = "",
            val avatarAuthStatus: String? = "",
            val avatar: String? = "",
            val userTags: List<UserTag>? = listOf()
    )

    data class UserTag(
            val tagId: String? = "",
            val tagName: String? = ""
    )

    data class UserReply(
            val Id: String? = "",
            val topicId: String? = "",
            val replyMessage: String? = "",
            val replyUserId: String? = "",
            val replyVirtualName: String? = "",
            val replyIsRead: String? = "",
            val replyType: String? = "",
            val IaddTimeStampd: String? = "",
            val nickname: String? = ""
    )

    data class UserVote(
            val Id: String? = "",
            val topicId: String? = "",
            val replyMessage: String? = "",
            val replyUserId: String? = "",
            val replyVirtualName: String? = "",
            val replyIsRead: String? = "",
            val replyType: String? = "",
            val addTimeStamp: String? = "",
            var nickname: String? = ""
    )
}