package com.dyyj.idd.chatmore.viewmodel

import android.widget.Toast
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.greenrobot.eventbus.EventBus


class FriendRequestViewModel : ViewModel() {

    fun doFriendRequest(requestId: String) {
        val subscribe = mDataRepository.doApplyFriendAction(requestId, "1").subscribe(
                {
                    if (it.errorCode == 200) {
                    } else {
                        Toast.makeText(ChatApp.mInstance?.applicationContext, it.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                },
                {
                    Toast.makeText(ChatApp.mInstance?.applicationContext, "网络异常", Toast.LENGTH_SHORT).show()
                }
        )
        CompositeDisposable().add(subscribe)
    }

    /**
     * 领取任务奖励
     */
    fun goRewardTask(taskId: String) {
        val subscribe = mDataRepository.getTaskRewardData(taskId!!).subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(ThreeDayViewModel.TaskRewardData(true, it.data))
                    } else {
                        EventBus.getDefault().post(ThreeDayViewModel.TaskRewardData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

}