package com.dyyj.idd.chatmore.model.network.result


data class ReportReasonResult(var errorCode: Int? = 0, var errorMsg: String? = "",
    var data: Data? = Data()) {

  data class Data(var reportReason: List<ReportReason>? = listOf()) {

    data class ReportReason(var reportReasonId: String? = "", var reportReasonName: String? = "")
  }
}