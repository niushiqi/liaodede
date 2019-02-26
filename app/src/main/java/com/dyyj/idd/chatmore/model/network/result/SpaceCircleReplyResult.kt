package com.dyyj.idd.chatmore.model.network.result

/**
 * 好友圈回复列表
 * Created by wangbin on 2018/12/27.
 */
data class SpaceCircleReplyResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: List<UserComment> = listOf()
) {

    data class UserComment(
            val id: String? = "",
            val avatar: String? = "",
            val topic_id: String? = "",
            val reply_message: String? = "",
//            val topicImgs: List<String>? = listOf(),
            val reply_user_id: String? = "",
            val reply_to_user_id: String? = "",
            val reply_id: String? = "",
            val reply_to_user_nickname: String? = "",
            val reply_virtual_name: String? = "",
            val reply_is_read: Int? = 0,
            val agreeAvatar: List<String>? = listOf(),
            val add_timestamp: String? = "",
            val reply_type: Int? = 0,
            val is_show: Int? = 0,
            val nickname: String? = ""
    )
}

/*
{
    "errorCode": 200,
    "errorMsg": "",
    "data": [{
    "avatar": "http:\/\/www.ldd.com\/avatar\/20181109160251_7112.jpg",
    "id": "84",
    "topic_id": "158",
    "reply_message": "\u54c8\u54c8\u54c8",
    "reply_user_id": "10363",
    "reply_to_user_id": "10129",
    "reply_virtual_name": "\u7a46\u5ff5\u6148",
    "reply_is_read": "1",
    "add_timestamp": "1546431514",
    "reply_type": "1",
    "is_show": "1",
    "nickname": "\u4e5d\u70b9\u534a\u5230\u5341\u70b9\u534a"
}, {
    "avatar": "http:\/\/www.ldd.com\/avatar\/20181109160251_7112.jpg",
    "id": "83",
    "topic_id": "158",
    "reply_message": "\u4f60\u8bf4\u4ec0\u4e48",
    "reply_user_id": "10363",
    "reply_to_user_id": "10129",
    "reply_virtual_name": "\u5efa\u5b81\u516c\u4e3b",
    "reply_is_read": "1",
    "add_timestamp": "1546431461",
    "reply_type": "1",
    "is_show": "1",
    "nickname": "\u4e5d\u70b9\u534a\u5230\u5341\u70b9\u534a"
}]
}*/
