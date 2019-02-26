package com.dyyj.idd.chatmore.model.network.result

data class EverydayTaskResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val taskList: List<EverydayTaskData>? = arrayListOf()
    ) {

        data class EverydayTaskData(
                val taskId: String? = "",
                val taskType: String? = "",
                val taskCategory: String? = "",
                val taskContent: String? = "",
                val taskSequence: Int = 0,
                val taskStatus: Int = 0,
                val taskExperience: Int = 0,
                val taskCash: String? = "",
                val taskCoin: Int = 0,
                val taskStone: Int = 0,
                val clickTarget: String? = ""
        )
    }
}