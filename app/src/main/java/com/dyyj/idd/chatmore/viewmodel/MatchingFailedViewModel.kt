package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/20
 * desc   : 匹配失败
 */
class MatchingFailedViewModel : ViewModel() {

    fun netRestoreMatchingStone() {
        val subscribe = mDataRepository.restoreMatchingStone().subscribe({
            EventBus.getDefault().postSticky(
                    RestoreMatchingStoneVM(it.errorCode == 200)
            )
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }
        }, {
            EventBus.getDefault().postSticky(
                    RestoreMatchingStoneVM(false))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 取消匹配
     */
    fun netCancelMatching() {
        val subscribe = mDataRepository.cancelMatching().subscribe({
            EventBus.getDefault().postSticky(MatchingFailedVM(it.errorCode == 200))
            if (it.errorCode != 200) {
                niceToast(it.errorMsg)
            }

        }, {
            EventBus.getDefault().postSticky(MatchingFailedVM(false))
        })
        mCompositeDisposable.add(subscribe)
    }

    /**
     * 挂电话
     */
    fun endCall() {
        mDataRepository.endCall()
    }

    class MatchingFailedVM(val isSuccess:Boolean)

    /**
     * 匹配失败返还魔石
     */
    class RestoreMatchingStoneVM(val isSuccess: Boolean)
}