package com.dyyj.idd.chatmore.model.network.result

data class LevelTaskResult(
        val errorCode: Int = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val taskList: List<LevelTaskData>? = arrayListOf()
    ) {

        data class LevelTaskData(
                val level: Int = 0,
                val levelExperience: Int = 0,
                val currentExperience: Int = 0,
                val clickTarget: String? = "",
                val taskCondition: TaskModel? = TaskModel()

        ) {
            data class TaskModel(
                    val taskName: String? = "",
                    val taskStatus: Int = 0,
                    val taskDesc: String? = ""
            )
        }

    }
}