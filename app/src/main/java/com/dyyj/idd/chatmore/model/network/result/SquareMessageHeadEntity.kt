package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by wangbin on 2018/12/29.
 */
data class SquareMessageHeadEntity(var type: Int = 0,
                                   var headImg: String? = null,
                                   var name: String? = null,
                                   var isTask: Boolean? = false,
                                   var age: Int? = 0
                                   )