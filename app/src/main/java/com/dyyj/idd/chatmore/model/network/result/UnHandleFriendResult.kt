package com.dyyj.idd.chatmore.model.network.result



data class UnHandleFriendResult(
    val errorCode: Int,
    val errorMsg: String,
    val data: List<Data>
)

data class Data(
    val requestId: String,
    val requestUserId: String,
    val toUserId: String,
    val requestMessage: Any,
    val requestTimestamp: String,
    val createTime: String,
    val handleResult: String,
    val handleMessage: Any,
    val handleTimestamp: String,
    val nickname: String,
    val gender: String,
    val userLevel: String,
    val age: Int,
    val birthday: String,
    val avatar: String
)