package com.dyyj.idd.chatmore.model.network.result

data class HankOpenResult(val errorCode: Int = 0,
                          val errorMsg: String? = "",
                          val data: Data? = Data()
) {
    data class Data(
            val bankIsOpen: Int? = 0
    )
}