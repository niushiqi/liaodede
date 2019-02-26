package com.dyyj.idd.chatmore.model.network.result


data class MatchingTipResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = Data()) {

  data class Data(var freezeCoin: String? = "", var envelopeTimes: Int? = 0,
      var matching: Matching? = Matching()) {

    data class Matching(var startTime: Int? = 0, var endTime: Int? = 0)
  }
}