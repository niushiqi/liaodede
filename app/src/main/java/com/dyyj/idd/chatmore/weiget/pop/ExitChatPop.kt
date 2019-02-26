package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopExitChatBinding
import com.dyyj.idd.chatmore.model.mqtt.result.ExitChatResult
import com.dyyj.idd.chatmore.viewmodel.CancelMatchingViewModel
import org.greenrobot.eventbus.EventBus

class ExitChatPop(context: Context) : BaseTipPop<PopExitChatBinding, CancelMatchingViewModel>(context) {

    fun initData() : ExitChatPop {
        mBinding.closeIv.setOnClickListener { dismiss() }
        mBinding.storeBtn.setOnClickListener { dismiss() }
        mBinding.taskBtn.setOnClickListener {
            EventBus.getDefault().post(ExitChatResult())
            dismiss()
        }
        return this@ExitChatPop
    }

    override fun onLayoutId(): Int {
        return R.layout.pop_exit_chat
    }

    override fun onViewModel(): CancelMatchingViewModel {
        return CancelMatchingViewModel()
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    fun show(view: View) {
        showAtLocation(view, Gravity.CENTER, 0, 0)
    }

}