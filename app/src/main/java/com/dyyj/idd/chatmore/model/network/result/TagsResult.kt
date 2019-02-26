package com.dyyj.idd.chatmore.model.network.result

data class TagsResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val tags: List<Tag>? = listOf()
    ) {
        data class Tag(
                val tagId: String? = "",
                val tagName: String? = "",
                var check: Boolean = false
        )
    }
}