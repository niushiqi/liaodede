package com.dyyj.idd.chatmore.model.network.result

import android.os.Build
import android.support.annotation.RequiresApi
import java.util.*

class aa {
    private val squareTopicCommentReplyId: String? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is aa) return false
        val aa = o as aa?
        return squareTopicCommentReplyId == aa!!.squareTopicCommentReplyId
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun hashCode(): Int {

        return Objects.hash(squareTopicCommentReplyId)
    }
}