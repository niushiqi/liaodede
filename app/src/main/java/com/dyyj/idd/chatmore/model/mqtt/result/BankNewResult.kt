package com.dyyj.idd.chatmore.model.mqtt.result

data class BankNewResult(
        val message: Msg? = Msg()
) {
    data class Msg(
            val line1: String? = ""
    )
}