package com.dyyj.idd.chatmore.model.mqtt.result

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class OfflineResult(var offlineReason: String? = "") : Parcelable