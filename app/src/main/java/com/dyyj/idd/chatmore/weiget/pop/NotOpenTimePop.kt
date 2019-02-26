package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopNotOpenBinding
import com.dyyj.idd.chatmore.utils.DateFormatter
import com.dyyj.idd.chatmore.viewmodel.NotOpenPopViewModel
import org.greenrobot.eventbus.Subscribe
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/18
 * desc   : 聊天场景,限时开放
 */
class NotOpenTimePop(context: Context) : BaseTipPop<PopNotOpenBinding, NotOpenPopViewModel>(
    context) {
  override fun onLayoutId(): Int {
    return R.layout.pop_not_open
  }

  override fun onViewModel(): NotOpenPopViewModel {
    return NotOpenPopViewModel()
  }


  override fun onInitListener() {
    super.onInitListener()
    mBinding.closeIv.setOnClickListener { dismiss() }
  }

  override fun onLayoutSet() {
    width = ViewGroup.LayoutParams.MATCH_PARENT
    height = ViewGroup.LayoutParams.MATCH_PARENT
  }

  override fun onInitView() {
    super.onInitView()
    onCreateEvenbus(this)
    setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.color.black_80))
    // 设置点击窗口外边窗口消失
    isOutsideTouchable = true
    // 设置此参数获得焦点，否则无法点击
    isFocusable = true
    mViewModel.netMatchingTip()

  }

  override fun dismiss() {
    super.dismiss()
    onDestryEvenbus(this)
  }

  fun show(view: View) {
    showAtLocation(view, Gravity.CENTER, 0, 0)
  }

  /**
   * 接收界面数据
   */
  @Subscribe
  fun onNotOpenVM(obj: NotOpenPopViewModel.NotOpenVM) {
    if (obj.isSuccess) {
      mBinding.coinTv.text = obj.obj?.freezeCoin ?: ""
      mBinding.boxTv.text = obj.obj?.envelopeTimes?.toString() ?: ""

      val nowTime = Date()
      System.out.println(nowTime)
      val time = SimpleDateFormat("HH : mm")
      val startTime = time.format(
          Date(DateFormatter.timestamp2Date(obj.obj?.matching?.startTime?.toString())))
      val endTime = time.format(
          Date(DateFormatter.timestamp2Date(obj.obj?.matching?.endTime?.toString())))
      mBinding.timeTv.text = "$startTime - $endTime"
    }
  }
}