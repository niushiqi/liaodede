package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel

class RequestVideoDialogViewModel: ViewModel() {
    fun netResponseSwitchVideo(talkId: String, fromUserId: String, toUserId: String, responseResult: String) {
        val subscribe = mDataRepository.responseSwitchVideo(talkId, fromUserId, toUserId, responseResult).subscribe(
                {}
        )
        mCompositeDisposable.add(subscribe)
    }
}