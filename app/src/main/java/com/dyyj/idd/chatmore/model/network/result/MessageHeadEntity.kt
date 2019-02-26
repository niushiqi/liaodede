package com.dyyj.idd.chatmore.model.network.result

/**
 * Created by Corey_Jia on 2018/12/16.
 */
data class MessageHeadEntity(var type: Int = 0,
                             var headImg: String? = null,
                             var name: String? = null,
                             var age: Int? = 0,
                             var isFriend: Boolean? = true)
