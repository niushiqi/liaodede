package com.dyyj.idd.chatmore.model.network.result

data class CurrentTaskListResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: List<Data>? = arrayListOf()
) {
    data class Data(
            val taskId: String? = "",
            val taskType: String? = "",
            val taskStatus: Int? = 0,
            val taskContent: String? = "",
            val taskSequence: String? = "",
            val taskCash: String? = "",
            val taskCoin: String? = "",
            val taskStone: String? = "",
            val clickTarget: String? = ""

    )
}