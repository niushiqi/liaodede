package com.dyyj.idd.chatmore.model.network.result



data class WithdrawRecordResult(
    val errorCode: Int? = 0,
    val errorMsg: String? = "",
    val data: List<recordData>? = arrayListOf()
) {
    data class recordData(
        val timeTip: String ?= "",
        val list: List<listData>? = arrayListOf()
    ){
        data class listData(
                val apply_id: String ?= "",
                val apply_biz_no: String ?= "",
                val apply_order_id: String ?= "",
                val apply_user_id: String ?= "",
                val apply_user_name: String ?= "",
                val apply_user_mobile: String ?= "",
                val apply_alipay_account: String ?= "",
                val apply_amount: String ?= "",
                val apply_status: String ?= "",
                val apply_pay_status: String ?= "",
                val apply_time: String ?= "",
                val apply_auth_time: String ?= "",
                val apply_pay_time: String ?= ""
        )
    }
}