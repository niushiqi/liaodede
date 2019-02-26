package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopMessageTipBinding
import com.dyyj.idd.chatmore.utils.DeviceUtils
import com.dyyj.idd.chatmore.viewmodel.MessageTipPopViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MessageTipPop(context: Context?) : BaseTipPop<PopMessageTipBinding, MessageTipPopViewModel>(
    context) {
  open fun initData(rewardId: String, title: String): MessageTipPop {
//        startTime()
    mBinding.tipTxt.text = title
    mBinding.actionTxt.setOnClickListener {
      mViewModel.getReward(rewardId)
    }
    return this@MessageTipPop
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
    EventBus.getDefault().register(this)
    showAtLocation(view, Gravity.TOP, 0, DeviceUtils.dp2px(view.resources, 92F).toInt())
  }

  open fun enableTime() : MessageTipPop {
    startTime()
    return this@MessageTipPop
  }

  override fun dismiss() {
    super.dismiss()
    EventBus.getDefault().unregister(this)
  }

  @Subscribe
  fun onGetRewardVM(obj: MessageTipPopViewModel.GetRewardVM) {
    dismiss()
  }
}