package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.TaskRewardResult
import com.dyyj.idd.chatmore.model.network.result.ThreeDayTaskResult
import org.greenrobot.eventbus.EventBus

class ThreeDayViewModel : ViewModel() {
    fun getTaskData() {
        val subscribe = mDataRepository.getThreeDayTaskData().subscribe(
                {
                    if (it.errorCode == 200 && it.data!!.taskList!!.size >= 0) {
                        EventBus.getDefault().post(TaskThreeDayData(true, it.data!!))
                    } else {
                        EventBus.getDefault().post(TaskThreeDayData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    class TaskThreeDayData(val success: Boolean, val obj: ThreeDayTaskResult.Data? = null)

    fun getTaskReward(taskData: ThreeDayTaskResult.Data.ThreeDayTaskData) {
        val subscribe = mDataRepository.getTaskRewardData(taskData.taskId!!).subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(TaskRewardData(true, it.data))
                        getTaskData()
                    } else {
                        EventBus.getDefault().post(TaskRewardData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)

    }

    class TaskRewardData(val success: Boolean, val obj: TaskRewardResult.Data? = null)


}