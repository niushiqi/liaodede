package com.dyyj.idd.chatmore.model.network.result

import android.os.Parcel
import android.os.Parcelable

data class RedPackageResult(val errorCode: Int? = 0, val errorMsg: String? = "",
    val data: List<RedPackageEntry>? = listOf()) {
  data class RedPackageEntry(val isBigPackage : Int = 0, val name: String, val status: Int, val tip: String,
      val clickTip: String, val icon: String, val smallTip: String, val smallIcon: String,
      val blackIcon: String, val startTime: String, val availableTime: String = "0", val giftId: Int,
      val openedNum: String, val overPersonNum: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString(), parcel.readInt(), parcel.readString(),
                                       parcel.readString(), parcel.readString(),
                                       parcel.readString(), parcel.readString(),
                                       parcel.readString(), parcel.readString(),
                                       parcel.readString(), parcel.readInt(), parcel.readString(),
                                       parcel.readString()) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
      dest?.writeInt(isBigPackage)
      dest?.writeString(name)
      dest?.writeInt(status)
      dest?.writeString(tip)
      dest?.writeString(clickTip)
      dest?.writeString(icon)
      dest?.writeString(smallTip)
      dest?.writeString(smallIcon)
      dest?.writeString(blackIcon)
      dest?.writeString(startTime)
      dest?.writeString(availableTime)
      dest?.writeInt(giftId)
      dest?.writeString(openedNum)
      dest?.writeString(overPersonNum)
    }

    override fun describeContents(): Int {
      return 0
    }

    companion object CREATOR : Parcelable.Creator<RedPackageEntry> {
      override fun createFromParcel(parcel: Parcel): RedPackageEntry {
        return RedPackageEntry(parcel)
      }

      override fun newArray(size: Int): Array<RedPackageEntry?> {
        return arrayOfNulls(size)
      }
    }
  }
}