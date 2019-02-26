package com.dyyj.idd.chatmore.model.network.result

data class GameInfoResult(
        var errorCode: Int? = 0, var errorMsg: String? = "",
        var data: Data? = Data()
) {
    data class Data(
            var gameId: String? = "",
            var aNum: String? = "",
            var bNum: String? = "",
            var rs: String? = ""
    )
}