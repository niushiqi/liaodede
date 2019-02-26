package com.dyyj.idd.chatmore.model.network.result

/**
 * 好友圈回复列表
 * Created by wangbin on 2018/12/27.
 */
data class SpaceUserInfoResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: UserTopicResult.UserInfo?
)
