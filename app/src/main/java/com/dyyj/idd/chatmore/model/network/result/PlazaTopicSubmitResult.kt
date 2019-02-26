package com.dyyj.idd.chatmore.model.network.result

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

/**
 * 帖子列表
 */
data class PlazaTopicSubmitResult(
        val errorCode: Int? = 200,
        val errorMsg: String? = "",
        var totalVote: ArrayList<TotalVote>? = ArrayList(),
        var shareImage: String? = ""
) {

    @Parcelize
    data class TotalVote(
            var num: Int = 0,
            var rules_title: String = ""
    ) : Parcelable

}