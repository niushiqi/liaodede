package com.dyyj.idd.chatmore.model.network.result


data class GetGiftResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = Data()) {

  data class Data(var openedNum: Int = 0, var isBigPackage : Int = 0, var coin: String? = "0", var stone: String? = "0", var cash: String? = "0",
      var freezeCoin: String? = "", var addCoin: String? = "", var tip: String? = "",
      var envelope: String? = "", var prop: List<Prop?>? = listOf()) {

    data class Prop(var name: String? = "", var icon: String? = "", var num: Int? = 0)
  }
}