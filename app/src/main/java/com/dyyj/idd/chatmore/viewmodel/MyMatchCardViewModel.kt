package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.MyMatchCardResult
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus


/**
 * author : zwj
 * time   : 2019/01/23
 * desc   : 我的匹配卡
 */

class MyMatchCardViewModel : ViewModel() {

    /**
     * 获取我的匹配卡
     */
    fun matchCardList() {
        val subscribe = mDataRepository.matchCardList().subscribe({
            EventBus.getDefault().post(MatchCardVM(it.errorCode == 200,it.data))
        }, {
            niceToast(it.message)
        })
        mCompositeDisposable.add(subscribe)
    }

    class MatchCardVM(val isSuccess: Boolean, val obj: List<MyMatchCardResult.Data>? = null)


}