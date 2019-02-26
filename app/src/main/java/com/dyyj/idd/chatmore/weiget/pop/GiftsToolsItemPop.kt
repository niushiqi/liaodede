package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopGameToolsItemBinding
import com.dyyj.idd.chatmore.model.network.result.MyGiftsResult
import com.dyyj.idd.chatmore.viewmodel.PopGiftsItemViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/18
 * desc   : 兑换金币
 */
open class GiftsToolsItemPop(context: Context?,
                             val obj: MyGiftsResult.Gift?) : BaseTipPop<PopGameToolsItemBinding, PopGiftsItemViewModel>(
    context) {
  override fun onLayoutId(): Int {
    return R.layout.pop_game_tools_item
  }

  override fun onViewModel(): PopGiftsItemViewModel {
    return PopGiftsItemViewModel()
  }

  override fun onLayoutSet() {
    width = ViewGroup.LayoutParams.MATCH_PARENT
    height = ViewGroup.LayoutParams.MATCH_PARENT
  }

  override fun onInitData() {
    super.onInitData()
    onCreateEvenbus(this)
    setBackgroundDrawable(ContextCompat.getDrawable(context!!, R.color.black_80))
    // 设置点击窗口外边窗口消失
    isOutsideTouchable = true
    // 设置此参数获得焦点，否则无法点击
    isFocusable = true
    initListener()
  }

  private fun initListener() {
    mBinding.addIv.setOnClickListener { add() }
    mBinding.deleteIv.setOnClickListener { delete() }
    mBinding.cancelBtn.setOnClickListener { dismiss() }
    mBinding.convertBtn.setOnClickListener {
      mViewModel.netExchange2Coin(mBinding.totalTv.text.toString(), obj?.id.toString())
    }
  }

  override fun dismiss() {
    super.dismiss()
    onDestryEvenbus(this)
  }

  private fun add() {
      val count = mBinding.totalTv.text.toString()
      if (count.toInt() + 5 > obj?.num!!) {
          return
      }
      val total = count.toInt() + 5
      mBinding.totalTv.text = "$total"
      mBinding.countTv.text = "可兑换：${(total * obj.coin?.toFloat()!!).toInt()}金币"
  }

  private fun delete() {
    val count = mBinding.totalTv.text.toString()
    if (count.toInt() - 5 < 5) {
      return
    }
    val total = count.toInt() - 5
    mBinding.totalTv.text = "$total"
    mBinding.countTv.text = "可兑换：${(total * obj?.coin?.toFloat()!!).toInt()}金币"
  }

  open fun show(view: View) {
    initData()
    showAtLocation(view, Gravity.CENTER, 0, 0)
    obj?.coin?.toFloat()?.let {
      mBinding.countTv.text = "可兑换：${(5 * it).toInt()}金币"
    }
  }

  private fun initData() {
      mBinding.titleTv.text = "用\"${obj?.name}\"兑换金币"
  }

  @Subscribe
  fun onExchange2CoinVM(obj: PopGiftsItemViewModel.Exchange2CoinVM) {
    if (obj.isSuucess) {
      dismiss()
    } else {
      context?.niceToast("兑换失败")
    }
  }
}