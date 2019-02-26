package com.dyyj.idd.chatmore.model.network.result


data class InviteCodeResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = null) {

  data class Data(var coin: String? = "", var cash: String? = "")
}