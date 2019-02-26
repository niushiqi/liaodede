package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2018/12/27.
 */
data class SpaceCircleResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: UserComment = UserComment()
) {

    data class UserComment(
            val Id: String? = "",
            val userId: String? = "",
            val topicType: Int? = 1,
            var isVote: Int? = 0,
            val topicBody: String? = "",
            val topicImgs: List<String>? = listOf(),
            var topicVoteCount: Int? = 0,
            val topicReplyCount:  Int? = 0,
            val topicTargetId: String? = "",
            val topicExt: String? = "",
            val agreeAvatar: List<String>? = listOf(),
            val addTimeStamp: String? = ""
    )
}

//{
//    "errorCode": 200,
//    "errorMsg": "",
//    "data": {
//    "Id": "158",
//    "userId": "10129",
//    "topicType": "1",
//    "topicBody": "\u5566\u5566\u5566",
//    "topicImgs": "20181230011022_1790.png",
//    "topicReplyCount": "2",
//    "topicVoteCount": "3",
//    "topicTargetId": "0",
//    "topicExt": "",
//    "addTimeStamp": "1546103424",
//    "agreeAvatar": ["http:\/\/www.ldd.com\/avatar\/20190101041118_3146.jpg", "http:\/\/www.ldd.com\/avatar\/20181109160251_7112.jpg", "http:\/\/www.ldd.com\/avatar\/20181227015929_8990.jpg"]
//}
//}