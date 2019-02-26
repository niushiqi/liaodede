package com.dyyj.idd.chatmore.model.network.result

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * 话题列表
 */
class PlazaTopicListResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: List<Topic>? = listOf()
) {
    @Parcelize
    data class Topic(
            @SerializedName("squareTopicId")
            val squareTopicId: String = "",
            val squareTopicTitle: String? = "",
            val squareTopicImage: String? = "",
            val squareTopicImageList: String? = "",
            val squareTopicContent: String? = "",
            var squareTopicIsHot: Int = 0,
            var squareTopicIsNew: Int = 1,
            var squareTopicAgreeNum: Int = 0,
            var squareTopicCommentsNum: Int = 0,
            var squareTopicIsTop: Int = 0,
            var squareTopicSort: Int = 0,
            var squareTopicDisplay: Int = 0,
            var participateInNum: Int = 0,//话题测试投票等人数
            val squareTopicPubTimestamp: String? = "",

            var top: Int = 0,
            var follow: Int = 0,

            var _FocusSort: Long = 0,
            var followTimestamp: Long = 0,
            var newComments: Int = 0,

            var squareTopicType: Int = 0, //0文本  1测试  2 投票
            var question: List<Question> = listOf(),

            var shareImage:String?="",
            var inputValue:String?=""
    ) : Parcelable

    @Parcelize
    data class Question(
            val id: String = "",
            val square_topic_id: String = "",
            val rules_id: String = "",
            val rules_sort: Int = 0,
            val rules_display: Int = 0,
            val add_timestamp: Long = 0L,
            val rules_title: String = "",
            val rules_type: Int = 0,//"2", //类型为输入 没有选项数据 "1", //测试类型话题
            val option: List<Option> = listOf()
    ) : Parcelable

    @Parcelize
    data class Option(
            val Id: String = "",
            val rulesTitle: String = "",
            val rulesType: Int = 0,
            val rulesImage: String = ""

    ) : Parcelable
}