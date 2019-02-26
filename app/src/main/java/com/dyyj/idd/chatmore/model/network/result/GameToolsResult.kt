package com.dyyj.idd.chatmore.model.network.result


data class GameToolsResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = Data()) {

  data class Data(var props: List<Prop>? = listOf(), var userStone: String? = "") {

    data class Prop(var id: String? = "", var name: String? = "", var icon: String? = "",
        var iconGray: String? = "", var tip: String? = "", var status: String? = "",
        var props: List<PropItem>? = listOf()) {

      data class PropItem(var id: String? = "", var name: String? = "", var cid: String? = "",
          var stone: String? = "", var coin: String? = "0", var cash: String? = "",
          var rare: String? = "", var icon: String? = "", var tip: String? = "",
          var status: String? = "", var ownNum: Int = 0)
    }
  }
}