package com.dyyj.idd.chatmore.model.network.result

import com.google.gson.annotations.SerializedName

/**
 * 帖子列表
 */
data class PlazaSpaceCardResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        var data: Data? = Data()
) {

    data class Data(
            var list: List<Detail> = listOf(),
            var total: Int = 0
    )

    /**
     * {"errorCode":200,"errorMsg":"","data":{"list":
     * [
     * {"Id":"189","userId":"10439","topicType":"1",
     * "topicBody":"\u6d4b\u8bd5","topicImgs":"20190105225543_5502.png"
     * ,"topicReplyCount":"0","topicVoteCount":"0","topicTargetId":"0"
     * "addTimeStamp":"1546700145",
     * "squareTopicCommentImage":["http:\/\/www.ldd.com\/upload\/topic\/20190105225543_5502.png"],
     * "squareTopicType":"2"},
     * {"Id":"6","dynamicType":"1","squareTopicId":"1",
     * "squareTopicTitle":"\u804a\u5f97\u5f97\u79d8\u7c4d",
     * "squareTopicContent":"\u4ea4\u53cb\u3001\u5347\u7ea7\u3001\u63d0\u73b0\u79d8\u7c4d\u5728\u6b64",
     * "squareTopicImage":"http:\/\/api.ddaylove.com\/upload\/square\/topicbg\/1.png",
     * "commentId":"390","squareTopicCommentId":"",
     * "squareTopicCommentReplyId":"",
     * "topicBody":"\u968f\u4fbf\u804a\u804a\u5427\uff01",
     * "squareTopicCommentImage":["http:\/\/www.ldd.com\/upload\/square\/20190105231207_9090.png","http:\/\/www.ldd.com\/upload\/square\/20190105231208_8693.png","http:\/\/www.ldd.com\/upload\/square\/20190105231211_9345.png"],
     * "addTimeStamp":"1546701132","topicAgreeCount":"0","topicReplyCount":"0","squareTopicType":"1"}],
     * "total":2}}
     */
    data class Detail(
            @SerializedName("Id")
            var id: String = "",
            var commentId: String = "",
            var squareTopicId: String = "",
            var squareTopicTitle: String = "",
//            var squareTopicContent: String = "",
            var topicBody: String = "",//文本内容
            var squareTopicCommentImage: List<String> = listOf(),
            var topicReplyCount: Int = 0,//评论数
            var topicVoteCount: Int = 0,//点赞数
            var topicAgreeCount: Int = 0,//点赞数
            var addTimeStamp: Long = 0,//
            var squareTopicType: Int = 1 //1、广场 2、好友圈
    )

}