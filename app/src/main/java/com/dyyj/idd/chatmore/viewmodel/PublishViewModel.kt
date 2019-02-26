package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PublishAdapter

class PublishViewModel: ViewModel() {

    private val mAdapter = lazy { PublishAdapter() }

    fun getAdapter(): PublishAdapter {
        return mAdapter.value
    }

    class PublishSuccess()

}