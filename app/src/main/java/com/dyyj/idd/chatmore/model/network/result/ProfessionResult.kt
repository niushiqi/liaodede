package com.dyyj.idd.chatmore.model.network.result


data class ProfessionResult(
    var errorCode: Int? = 0,
    var errorMsg: String? = "",
    var data: Data? = Data()
) {

    data class Data(
        var professionList: List<Profession?>? = listOf()
    ) {

        data class Profession(
            var professionId: String? = "",
            var professionName: String? = ""
        )
    }
}