package com.dyyj.idd.chatmore.model.network.result

data class MyHeBaoResult(var errorCode: Int? = 0, var errorMsg: String? = "",
                         var data: Data? = Data()) {

  data class Data(val stone: Int? = 0, val deDou: String? = "",var matchCard: String? = "")
}