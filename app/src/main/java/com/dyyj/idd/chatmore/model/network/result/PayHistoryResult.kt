package com.dyyj.idd.chatmore.model.network.result



data class PayHistoryResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: List<recordData>? = arrayListOf()
) {
    data class recordData(
            val timeTip: String? = "",
            val list: List<listData>? = arrayListOf()
    ) {
        data class listData(
                val orderBizNo: String? = "",
                val orderPrice: String? = "",
                val orderDesc: String? = "",
                val orderAlipayAccount: String? = "",
                val orderGoodsName: String? = "",
                val orderTime: String? = "",
                val orderStatus: String? = "",
                val orderPayType: String? = ""
        )
    }
}