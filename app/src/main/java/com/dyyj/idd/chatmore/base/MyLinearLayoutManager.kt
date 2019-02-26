package com.dyyj.idd.chatmore.base

import android.content.Context
import android.support.v7.widget.LinearLayoutManager

class MyLinearLayoutManager(context: Context): LinearLayoutManager(context) {
    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }
}