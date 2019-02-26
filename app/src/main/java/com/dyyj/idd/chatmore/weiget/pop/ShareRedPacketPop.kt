package com.dyyj.idd.chatmore.weiget.pop

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopShareRedPacketBinding
import com.dyyj.idd.chatmore.utils.ActManagerUtils
import com.dyyj.idd.chatmore.utils.DisplayUtils
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.OpenCallViewModel
import com.dyyj.idd.chatmore.viewmodel.ShareRedPacketViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/09/13
 * desc   : 分享红包
 */
class ShareRedPacketPop(
    context: Context?) : BaseTipPop<PopShareRedPacketBinding, ShareRedPacketViewModel>(context) {
  override fun onLayoutId(): Int {
    return R.layout.pop_share_red_packet
  }

  override fun onViewModel(): ShareRedPacketViewModel {
    return ShareRedPacketViewModel()
  }

  override fun onLayoutSet() {
    width = DisplayUtils.dp2px(context, 210.toFloat())
    height = DisplayUtils.dp2px(context, 290.toFloat())
  }

  override fun onInitListener() {
    super.onInitListener()
    mBinding.btnClose.setOnClickListener { dismiss() }
    mBinding.btnShare.setOnClickListener {
      mViewModel.netShareMessage()
//      dismiss()
    }
  }

  override fun onInitView() {
    super.onInitView()
    onCreateEvenbus(this)
    setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.color.black_80))
    // 设置点击窗口外边窗口消失
    isOutsideTouchable = true
    // 设置此参数获得焦点，否则无法点击
    isFocusable = true
  }

  /**
   * POP消失
   */
  override fun dismiss() {
//    EventBus.getDefault().post(OpenCallViewModel.ShareStatusVM(true))
    onDestryEvenbus(this)
    super.dismiss()
    EventBus.getDefault().post(OpenCallViewModel.ShareStatusVM(false))
  }

  /**
   * 显示
   */
  fun show(activity: Activity, view: View) {
    showAtLocation(view, Gravity.CENTER, 0, 0)
  }

  @Subscribe
  fun onEmpty(pop: ShareRedPacketPop) {

  }

  @Subscribe
  fun onShareMessageVM(obj: ShareRedPacketViewModel.ShareMessageVM) {
    if (obj.success) {
      dismiss()
      var mPop = SocialSharePop(ActManagerUtils.getAppManager().currentActivity()!!)
      mPop.initWebPacketListener(ActManagerUtils.getAppManager().currentActivity()!!, obj.inviteCode, obj.shareUrl, obj.title, obj.icon)
      mPop.show(mBinding.root)
      //ShareUtils.shareWeb3(ActManagerUtils.getAppManager().currentActivity()!!, obj.inviteCode, obj.shareUrl, obj.title, obj.icon, false)
    }
  }


}