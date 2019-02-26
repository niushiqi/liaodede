package com.dyyj.idd.chatmore.model.network.result

/**
 * 帖子列表
 */
data class PlazaFlowCardResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        val data: Data? = null
) {
    data class Data(
            val comments: ArrayList<PlazaCardResult.PlazaTopic>? = arrayListOf(),
            val topic: PlazaTopicListResult.Topic? = null
    )
}