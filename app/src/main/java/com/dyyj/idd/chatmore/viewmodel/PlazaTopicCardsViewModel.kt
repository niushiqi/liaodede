package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.PlazaCardResult
import com.dyyj.idd.chatmore.ui.adapter.PlazaCardAdapter
import org.greenrobot.eventbus.EventBus

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   :
 */
class PlazaTopicCardsViewModel : ViewModel() {
    var type = 0 // 0为热门  1为新帖
    var topicID = ""

    private val mAdapter by lazy { PlazaCardAdapter() }

    fun getAdapter() = mAdapter

}