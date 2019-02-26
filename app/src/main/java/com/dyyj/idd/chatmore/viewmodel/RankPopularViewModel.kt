package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.PopularityTopResult
import com.dyyj.idd.chatmore.ui.adapter.RankPopularAdapter
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class RankPopularViewModel: ViewModel() {
    private val mAdapter by lazy { RankPopularAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    /**
     * 好友列表
     */
    fun netRankList(page: Int, pageSize: Int) {
        mDataRepository.getPopulatiryTopList(mDataRepository.getUserid()!!, page.toString(), pageSize.toString())
                .subscribe({
                    EventBus.getDefault().post(RankPopularVM(it, it.errorCode == 200, page != 1))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(RankPopularVM(null, false, page != 1))
                })
    }

    class RankPopularVM(val obj: PopularityTopResult? = null, val isSuccess: Boolean = false, val more: Boolean)
}