package com.dyyj.idd.chatmore.model.network.result


data class UploadAvatarResult(val errorCode: Int? = 0, val errorMsg: String? = "",
    val data: Data? = Data()) {
  data class Data(val avatar: String? = "",
      val avatarFilename: String? = "")
}