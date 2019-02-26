package com.dyyj.idd.chatmore.viewmodel

import android.widget.Toast
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import org.greenrobot.eventbus.EventBus

class MessageTipPopViewModel : ViewModel() {
    fun getReward(rewardId: String) {
        val subscribe = mDataRepository.postGetReward(rewardId).subscribe(
                {
                    if (it.errorCode == 200) {
                        Toast.makeText(ChatApp.mInstance?.applicationContext, "领取成功", Toast.LENGTH_SHORT).show()
                    } else {
//                        Toast.makeText(ChatApp.mInstance?.applicationContext, "领取失败", Toast.LENGTH_SHORT).show()
                    }
                  EventBus.getDefault().post(GetRewardVM(it.errorCode == 200))
                }
        )
        mCompositeDisposable.add(subscribe)
    }

    class GetRewardVM(val isSuccess:Boolean = false)
}