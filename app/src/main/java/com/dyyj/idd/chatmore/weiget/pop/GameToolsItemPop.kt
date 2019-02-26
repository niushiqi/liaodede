package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopGameToolsItemBinding
import com.dyyj.idd.chatmore.model.network.result.GameToolsResult
import com.dyyj.idd.chatmore.viewmodel.PopGameToolsItemViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/25
 * desc   : 兑换金币
 */
open class GameToolsItemPop(context: Context?,
    val obj: GameToolsResult.Data.Prop.PropItem) : BaseTipPop<PopGameToolsItemBinding, PopGameToolsItemViewModel>(
    context) {
  override fun onLayoutId(): Int {
    return R.layout.pop_game_tools_item
  }

  override fun onViewModel(): PopGameToolsItemViewModel {
    return PopGameToolsItemViewModel()
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
      mViewModel.netConvert2Coin(mBinding.totalTv.text.toString(), obj.id.toString())
    }



  }

  override fun dismiss() {
    super.dismiss()
    onDestryEvenbus(this)
  }

  private fun add() {
    val count = mBinding.totalTv.text.toString()
    if (count.toInt() + 10 > obj.ownNum) {
      return
    }
    val total = count.toInt() + 10
    mBinding.totalTv.text = "$total"
    mBinding.countTv.text = "可兑换：${(total * obj.coin?.toFloat()!!).toInt()}金币"
  }

  private fun delete() {
    val count = mBinding.totalTv.text.toString()
    if (count.toInt() - 10 < 30) {
      return
    }
    val total = count.toInt() - 10
    mBinding.totalTv.text = "$total"
    mBinding.countTv.text = "总价值：${(total * obj.coin?.toFloat()!!).toInt()}金币"
  }

  open fun show(view: View) {
    mBinding.titleTv.text = "用\"${obj?.name}\"兑换金币"
    showAtLocation(view, Gravity.CENTER, 0, 0)
    obj?.coin?.toFloat()?.let {
      mBinding.countTv.text = "可兑换：${(30 * it).toInt()}金币"
    }
  }


  @Subscribe
  fun onConvert2CoinVM(obj: PopGameToolsItemViewModel.Convert2CoinVM) {
    if (obj.isSuucess) {
      context?.niceToast("兑换成功,金币已入帐")
      dismiss()
    } else {
      context?.niceToast("兑换失败")
    }
  }
}