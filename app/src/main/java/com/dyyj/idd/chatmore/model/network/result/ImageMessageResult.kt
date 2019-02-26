package com.dyyj.idd.chatmore.model.network.result

data class ImageMessageResult(val errorCode: Int? = 0, val errorMsg: String? = "",
                              var data: Data? = Data()) {
  data class Data(var image: String? = "",
      var imageFilename: String? = "")
}