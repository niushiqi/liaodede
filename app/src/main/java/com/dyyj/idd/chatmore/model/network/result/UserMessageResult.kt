package com.dyyj.idd.chatmore.model.network.result

data class UserMessageResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val unReadCount: Int? = 0,
        val firstInfo: FirstInfo? = FirstInfo()
) {
    data class FirstInfo(
            val avatar: String? = "",
            val topic_info: TopicInfo? = TopicInfo(),
            val base_info: BaseInfo? = BaseInfo(),
            val userBaseInfo: UserBaseInfo? = UserBaseInfo()
    )

    data class TopicInfo(
        val id: String? = "",
        val user_id: String? = "",
        val topic_type: String? = "",
        val topic_body: String? = "",
        val topic_imgs: String? = "",
        val topic_reply_count: String? = "",
        val topic_vote_count: String? = "",
        val topic_target_id: String? = "",
        val add_timestamp: String? = "",
        val topic_ext: String? = "",
        val is_show: String? = ""
    )

    data class BaseInfo(
        val id: String? = "",
        val user_id: String? = "",
        val log_type: String? = "",
        val item_id: String? = "",
        val add_timestamp: String? = ""
    )

    data class UserBaseInfo(
//            "nickname": "12221",
//    "gender": "2",
//    "userExperience": "168",
//    "userLevel": "3",
//    "isVip": "0",
//    "birthday": "1995-01-01",
//    "avatar": "20181009141921_3188.jpg",
//    "avatar_auth_status": "1"
        val nickname: String? = "",
        val gender: String? = "",
        val userExperience: String? = "",
        val userLevel: String? = "",
        val isVip: String? = "",
        val birthday: String? = "",
        val avatar: String? = "",
        val avatar_auth_status: String? = ""
    )
}