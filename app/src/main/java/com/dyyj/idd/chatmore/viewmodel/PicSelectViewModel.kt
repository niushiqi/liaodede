package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.user.photo.ImageAdapter

class PicSelectViewModel: ViewModel() {

    private val mAdapter = lazy { ImageAdapter() }

    open fun getAdapter() : ImageAdapter {
        return mAdapter.value
    }

}