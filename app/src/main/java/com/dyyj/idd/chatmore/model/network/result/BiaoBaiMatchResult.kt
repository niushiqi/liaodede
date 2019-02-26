package com.dyyj.idd.chatmore.model.network.result

data class BiaoBaiMatchResult(
        val errorCode: Int?= 0,
        val errorMsg: String? = "",
        val data: List<String>? = listOf()
)