package com.dyyj.idd.chatmore.model.network.result

/**
 * 话题列表
 */
class PlazaTopicResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: PlazaTopicListResult.Topic? = null
)