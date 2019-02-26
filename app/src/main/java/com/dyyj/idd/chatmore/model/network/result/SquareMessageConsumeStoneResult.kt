package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2019/1/3.
 */
data class SquareMessageConsumeStoneResult(
        val errorCode: Int? = 0,
        val errorMsg: String? = "",
        val data: Data? = Data()
){
    data class Data(
            val consumeStone: Int? = 10
    )
}