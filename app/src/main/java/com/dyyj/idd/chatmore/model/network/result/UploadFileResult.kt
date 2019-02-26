package com.dyyj.idd.chatmore.model.network.result

data class UploadFileResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
) {
    data class Data(
            val imgUrl: String? = "",
            val imgFilename: String? = ""
    )
}