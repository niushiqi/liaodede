package com.dyyj.idd.chatmore.weiget.pop

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.databinding.PopRedPacketBinding
import com.dyyj.idd.chatmore.viewmodel.RedPacketPopViewModel2
import com.gt.common.gtchat.extension.niceGlide
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/18
 * desc   : 领取红包
 */
class RedPacketPop2(context: Context?) : BaseTipPop<PopRedPacketBinding, RedPacketPopViewModel2>(
    context) {
  override fun onLayoutId(): Int {
    return R.layout.pop_red_packet
  }

  override fun onViewModel(): RedPacketPopViewModel2 {
    return RedPacketPopViewModel2()
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
  }

  /**
   * 显示
   */
  fun show(view: View, giftId: String) {
    mViewModel.netGetGift(giftId)
    initListener()
    showAtLocation(view, Gravity.CENTER, 0, 0)
  }


  /**
   * 点击事件
   */
  fun initListener() {

    mBinding.getBtn.setOnClickListener {
      closeDialog()
    }
    mBinding.closeIv.setOnClickListener { closeDialog() }

  }

  /**
   * 关闭窗口,播放动画
   */
  private fun closeDialog() {
    //领取奖励
    dismiss()
  }

  /**
   * POP消失
   */
  override fun dismiss() {
    onDestryEvenbus(this)
    super.dismiss()

    if (mViewModel.mData != null) {
      EventBus.getDefault().post(RedPacketPopViewModel2.GetGiftOkVM(mViewModel.mData!!))
    } else {
      EventBus.getDefault().post(RedPacketPopViewModel2.GetGiftOkVM())
    }
  }

  /**
   * 领取红包
   */
  @Subscribe
  fun onGetGiftVM(obj: RedPacketPopViewModel2.GetGiftVM) {
    if (obj.isSuccess) {

//      obj.obj?.let {
      if (obj.obj != null) {
        //金币/魔石/现金
        mBinding.coinTv.text = "+${obj.obj.coin}"
        mBinding.stoneTv.text = "+${obj.obj.stone}"
        mBinding.cashTv.text = "+${obj.obj.cash}"
        mBinding.tipTv.text = obj.obj.tip
//        mBinding.titleTv.text = obj.obj.envelope
        setTextFont(mBinding.coinTv)
        setTextFont(mBinding.stoneTv)
        setTextFont(mBinding.cashTv)
        if (obj.obj.coin?.toFloat() == 0f) {
          mBinding.coinLl.visibility = View.GONE
        }
        if (obj.obj.stone?.toFloat() == 0f) {
          mBinding.stoneLl.visibility = View.GONE
        }
        if (obj.obj.cash?.toFloat() == 0f) {
          mBinding.cashLl.visibility = View.GONE
        }
        obj.obj.prop ?: return
        if (obj.obj.prop?.size == 0) {
          return
        }

        //道具
        for (i in 0..(obj.obj.prop?.size!! - 1)) {
          val prop = obj.obj.prop?.get(i)
          when (i) {
            0 -> {
              mBinding.ll1.visibility = View.VISIBLE
              mBinding.packet1Tv.text = "+${prop?.num}"
              setTextFont(mBinding.packet1Tv)
              mBinding.packet1Iv.niceGlide().load(prop?.icon).into(mBinding.packet1Iv)
            }
            1 -> {
              mBinding.ll2.visibility = View.VISIBLE
              mBinding.packet2Tv.text = "+${prop?.num}"
              setTextFont(mBinding.packet2Tv)
              mBinding.packet2Iv.niceGlide().load(prop?.icon).into(mBinding.packet2Iv)
            }
            2 -> {
              mBinding.ll3.visibility = View.VISIBLE
              mBinding.packet3Tv.text = "+${prop?.num}"
              setTextFont(mBinding.packet3Tv)
              mBinding.packet3Iv.niceGlide().load(prop?.icon).into(mBinding.packet3Iv)
            }
          }
        }
      }
    }
  }
}