package com.dyyj.idd.chatmore.model.network.result

data class MyMatchCardResult(var errorCode: Int? = 0, var errorMsg: String? = "",
                             var data: List<Data>? = listOf<Data>()) {

  data class Data(
          val name: String? = "",
          val backgroundImg: String? = "",
          val startTime: String? = "",
          var endTime: String? = ""
  )
}
