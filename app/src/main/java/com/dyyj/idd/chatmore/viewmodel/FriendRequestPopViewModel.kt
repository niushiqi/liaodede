package com.dyyj.idd.chatmore.viewmodel

import android.widget.Toast
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.mqtt.result.FriendRequestResult
import io.reactivex.disposables.CompositeDisposable

class FriendRequestPopViewModel : ViewModel() {
    var mData: FriendRequestResult? = null

    fun netDoFriendRequest() {
        val subscribe = mDataRepository.doApplyFriendAction(mData?.requestId!!, "1").subscribe(
                {
                    if (it.errorCode == 200) Toast.makeText(ChatApp.mInstance?.applicationContext, "ok", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(ChatApp.mInstance?.applicationContext, it.errorMsg, Toast.LENGTH_SHORT).show()
//                    if (it.errorCode != 200) {
//                        niceToast(it.errorMsg)
//                    }
                },
                {Toast.makeText(ChatApp.mInstance?.applicationContext, "网络异常", Toast.LENGTH_SHORT).show()}
        )
//        mCompositeDisposable.add(subscribe)
        CompositeDisposable().add(subscribe)
    }
}