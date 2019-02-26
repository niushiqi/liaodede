package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.BankTopResult
import com.dyyj.idd.chatmore.ui.adapter.RankBankAdapter
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

class RankBankViewModel: ViewModel() {
    private val mAdapter by lazy { RankBankAdapter() }

    /**
     * 获取Adapter
     */
    fun getAdapter() = mAdapter

    /**
     * 好友列表
     */
    fun netRankList(page: Int, pageSize: Int) {
        mDataRepository.getBankTopList(mDataRepository.getUserid()!!, page.toString(), pageSize.toString())
                .subscribe({
                    EventBus.getDefault().post(RankBankVM(it, it.errorCode == 200, page != 1))
                    if (it.errorCode != 200) {
                        niceToast(it.errorMsg)
                    }
                }, {
                    EventBus.getDefault().post(RankBankVM(null, false, page != 1))
                })
    }

    class RankBankVM(val obj: BankTopResult? = null, val isSuccess: Boolean = false, val more: Boolean)
}