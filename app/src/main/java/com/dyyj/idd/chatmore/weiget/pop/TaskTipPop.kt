package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopMessageTipBinding
import com.dyyj.idd.chatmore.ui.event.ReceiveTaskEvent
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.viewmodel.MessageTipPopViewModel
import org.greenrobot.eventbus.EventBus

class TaskTipPop(context: Context?) : BaseTipPop<PopMessageTipBinding, MessageTipPopViewModel>(
        context) {
    open fun initData(taskId: String, title: String): TaskTipPop {
//        startTime()
        mBinding.tipTxt.text = title
        mBinding.actionTxt.setOnClickListener {
            EventBus.getDefault().post(ReceiveTaskEvent(taskId))
            dismiss()
        }
        return this@TaskTipPop
    }

    override fun onLayoutId(): Int {
        return R.layout.pop_message_tip
    }

    override fun onViewModel(): MessageTipPopViewModel {
        return MessageTipPopViewModel()
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun show(view: View) {
        showAtLocation(view, Gravity.TOP, 0, DeviceUtils.dp2px(view.resources, 92F).toInt())

    }

    open fun enableTime(): TaskTipPop {
        startTime()
        return this@TaskTipPop
    }

}