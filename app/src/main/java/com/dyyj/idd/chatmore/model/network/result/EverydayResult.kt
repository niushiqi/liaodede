package com.dyyj.idd.chatmore.model.network.result

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/29
 * desc   : 日常任务
 */
data class EverydayResult(val rewardCoin: String? = "", val rewardStone: String? = "",
    val rewardCash: String? = "", val title: String? = "", val status: Int = 0, val type: Int? = 1)