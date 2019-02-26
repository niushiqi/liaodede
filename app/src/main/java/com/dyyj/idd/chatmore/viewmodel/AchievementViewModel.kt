package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.AchievementTaskResult
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/29
 * desc   : 成就任务
 */
class AchievementViewModel : ViewModel() {
    fun getTaskData() {
        val subscribe = mDataRepository.getAchievementTaskData().subscribe(
                {
                    if (it.errorCode == 200 && it.data!!.taskList!!.size >= 0) {
                        EventBus.getDefault().post(TaskAchievementData(true, it.data!!.taskList))
                    } else {
                        EventBus.getDefault().post(TaskAchievementData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    class TaskAchievementData(val success: Boolean, val obj: List<AchievementTaskResult.Data.AchievementTaskData>? = null)


    fun getTaskReward(taskData: AchievementTaskResult.Data.AchievementTaskData) {
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