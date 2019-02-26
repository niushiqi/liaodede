package com.dyyj.idd.chatmore.viewmodel

import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.model.network.result.LevelTaskResult
import org.greenrobot.eventbus.EventBus

/**
 * author : malibo
 * time   : 2018/10/01
 * desc   :
 */
class DialogTixianViewModel: ViewModel() {
    fun getTaskData() {
        val subscribe = mDataRepository.getLevelTaskData().subscribe(
                {
                    if (it.errorCode == 200 && it.data!!.taskList!!.size >= 0) {
                        EventBus.getDefault().post(DialogTixianViewModel.TaskLevelData(true, it.data!!.taskList))
                    } else {
                        EventBus.getDefault().post(DialogTixianViewModel.TaskLevelData(false))
                    }
                },
                { EventBus.getDefault().post(false) }
        )
        mCompositeDisposable.add(subscribe)
    }

    class TaskLevelData(val success: Boolean, val obj: List<LevelTaskResult.Data.LevelTaskData>? = null)
}