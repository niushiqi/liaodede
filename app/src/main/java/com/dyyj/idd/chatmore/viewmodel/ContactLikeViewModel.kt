package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.ContactLikeAdapter

class ContactLikeViewModel : ViewModel() {
    private val mAdapter by lazy { ContactLikeAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter
}