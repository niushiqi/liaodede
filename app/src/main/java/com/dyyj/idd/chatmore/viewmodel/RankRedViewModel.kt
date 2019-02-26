package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.RedTopData
import com.dyyj.idd.chatmore.ui.adapter.RankRedAdapter
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class RankRedViewModel: ViewModel() {
    private val mAdapter by lazy { RankRedAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    fun netRankList(page: Int, pageSize: Int) {
        mDataRepository.getRedTopList(mDataRepository.getUserid()!!, page.toString(), pageSize.toString())
                .subscribe({
                    EventBus.getDefault().post(RankRedVM(it, it.errorCode == 200, page != 1))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(RankRedVM(null, false, page != 1))
                })
    }

    class RankRedVM(val obj: RedTopData? = null, val isSuccess: Boolean = false, val more: Boolean)
}