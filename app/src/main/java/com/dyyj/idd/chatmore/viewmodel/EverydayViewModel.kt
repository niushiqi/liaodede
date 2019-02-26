package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.EverydayTaskResult
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/29
 * desc   : 日常任务
 */
class EverydayViewModel : ViewModel() {
    fun getTaskData() {
        val subscribe = mDataRepository.getEverydayTaskData().subscribe(
                {
                    if (it.errorCode == 200 && it.data!!.taskList!!.size >= 0) {
                        EventBus.getDefault().post(TaskEverydayData(true, it.data!!.taskList))
                    } else {
                        EventBus.getDefault().post(TaskEverydayData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    class TaskEverydayData(val success: Boolean, val obj: List<EverydayTaskResult.Data.EverydayTaskData>? = null)


    fun getTaskReward(taskData: EverydayTaskResult.Data.EverydayTaskData) {
        val subscribe = mDataRepository.getTaskRewardData(taskData.taskId!!).subscribe(
                {
                    if (it.errorCode == 200) {
                        EventBus.getDefault().post(ThreeDayViewModel.TaskRewardData(true, it.data))
                        getTaskData()
                    } else {
                        EventBus.getDefault().post(ThreeDayViewModel.TaskRewardData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)

    }
}