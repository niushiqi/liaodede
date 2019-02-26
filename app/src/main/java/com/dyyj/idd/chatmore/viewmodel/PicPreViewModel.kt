package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PrePhotoAdapter

class PicPreViewModel: ViewModel() {
    private val mAdapter = lazy { PrePhotoAdapter() }

    fun getAdapter(): PrePhotoAdapter {
        return mAdapter.value
    }
}