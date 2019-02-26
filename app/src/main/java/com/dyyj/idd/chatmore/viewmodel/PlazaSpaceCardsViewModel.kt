package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.PlazaCardResult
import com.dyyj.idd.chatmore.ui.adapter.PlazaCardAdapter
import com.dyyj.idd.chatmore.ui.adapter.PlazaSpaceCardAdapter
import org.greenrobot.eventbus.EventBus

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 我的空间 动态
 */
class PlazaSpaceCardsViewModel : ViewModel() {
    var usetID = ""

    private val mAdapter by lazy { PlazaSpaceCardAdapter(usetID) }

    fun getAdapter() = mAdapter

}