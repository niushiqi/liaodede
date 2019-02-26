package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.LevelTaskResult
import org.greenrobot.eventbus.EventBus

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/29
 * desc   : 等级任务
 */
class LevelViewModel:ViewModel() {
    fun getTaskData() {
        val subscribe = mDataRepository.getLevelTaskData().subscribe(
                {
                    if (it.errorCode == 200 && it.data!!.taskList!!.size >= 0) {
                        EventBus.getDefault().post(TaskLevelData(true, it.data!!.taskList))
                    } else {
                        EventBus.getDefault().post(TaskLevelData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }
    class TaskLevelData(val success: Boolean, val obj: List<LevelTaskResult.Data.LevelTaskData>? = null)

}