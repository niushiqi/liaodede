package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.NewbieGuideAdapter

/**
 * Created by wangbin on 2019/1/19.
 */
class NewbieGuideViewModel: ViewModel() {
    private val mAdapter by lazy { NewbieGuideAdapter() }

    fun getAdapter() = mAdapter
}