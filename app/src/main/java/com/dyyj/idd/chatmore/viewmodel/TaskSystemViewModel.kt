package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.SignInStatusResult
import com.dyyj.idd.chatmore.model.network.result.ThreeDayTaskResult
import org.greenrobot.eventbus.EventBus

/**
 * author :
 * e-mail : 714610354@qq.com
 * time   : 2018/06/28
 * desc   :
 */
class TaskSystemViewModel : ViewModel() {

    var signIn: SignInStatusResult.Data? = null

    fun netTask() {
        val subscribe = mDataRepository.getSigeInStatus().subscribe(
                {
                    if (it.errorCode == 200) {
                        signIn = it.data
                        EventBus.getDefault().post(TaskHeadVM(true, it.data))
                    } else {
                        EventBus.getDefault().post(TaskHeadVM(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    class TaskHeadVM(val success: Boolean, val obj: SignInStatusResult.Data? = null)

    class TaskThreeDayVM(val success: Boolean, val obj: List<ThreeDayTaskResult.Data.ThreeDayTaskData>? = null)

    fun getThreeDayTask() {
        val subscribe = mDataRepository.getThreeDayTaskData().subscribe(
                {
                    if (it.errorCode == 200 && it.data!!.taskList!!.size >= 0) {
                        EventBus.getDefault().post(TaskThreeDayVM(true, it.data!!.taskList))
                    } else {
                        EventBus.getDefault().post(TaskThreeDayVM(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }
}