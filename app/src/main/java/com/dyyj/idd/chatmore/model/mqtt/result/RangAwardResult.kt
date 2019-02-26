package com.dyyj.idd.chatmore.model.mqtt.result

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * 随机奖励
 */
@Parcelize
data class RangAwardResult(val rewardCoin: Double? = 0.0, val rewardStone: Double? = 0.0,
    val rewardCash: Double? = 0.0, val rewardId: String? = ""): Parcelable