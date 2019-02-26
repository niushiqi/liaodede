package com.dyyj.idd.chatmore.ui.dialog.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.DialogHangUpBinding
import com.dyyj.idd.chatmore.viewmodel.HangUpViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/17
 * desc   : 电话挂断奖励
 */
class HangUpActivity : BaseActivityV2<DialogHangUpBinding, HangUpViewModel>() {
  companion object {
    const val TYPE_GOLD = "gold"
    const val TYPE_STONE = "stone"
    const val TYPE_MONEY = "money"
    fun start(context: Context, gold: Double?, stone: Double?, money: Double?) {
      val intent = Intent(context, HangUpActivity::class.java)
      intent.putExtra(TYPE_GOLD, gold)
      intent.putExtra(TYPE_STONE, stone)
      intent.putExtra(TYPE_MONEY, money)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.dialog_hang_up
  }

  override fun onViewModel(): HangUpViewModel {
    return HangUpViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initView()
  }


  /**
   * 金币
   */
  private fun getGold() = intent.getDoubleExtra(TYPE_GOLD, 0.0)

  /**
   * 魔石
   */
  private fun getStone() = intent.getDoubleExtra(TYPE_STONE, 0.0)

  /**
   * 现金
   */
  private fun getMoney() = intent.getDoubleExtra(TYPE_MONEY, 0.0)

  @SuppressLint("SetTextI18n")
  private fun initView() {

    mBinding.itemCl.setOnClickListener { onBackPressed() }
    mBinding.closeBtn.setOnClickListener { onBackPressed() }

    mBinding.gold = getGold()
    mBinding.stone = getStone()
    mBinding.money = getMoney()

    mBinding.goldTv.text = "+${getGold()}"
    mBinding.stoneTv.text = "+${getStone()}"
    mBinding.moneyTv.text = "+${getMoney()}"

    setTextFont(mBinding.goldTv)
    setTextFont(mBinding.stoneTv)
    setTextFont(mBinding.moneyTv)
    initBg()
  }

  /**
   * 初始化背景
   */
  private fun initBg() {

    var count = 0
    if (getGold() > 0) count++
    if (getMoney() > 0) count++
    if (getStone() > 0) count++

    if (count == 0) {
      onBackPressed()
    }else if (count == 1) {
      mBinding.hangUpIv1.setImageResource(R.drawable.bg_hang_up_normal1_3)
      mBinding.hangUpIv3.setImageResource(R.drawable.bg_hang_up_normal3_3)
    }else if (count == 2) {
      mBinding.hangUpIv1.setImageResource(R.drawable.bg_hang_up_normal1_2)
      mBinding.hangUpIv3.setImageResource(R.drawable.bg_hang_up_normal3_2)
    }else if (count == 3) {
      mBinding.hangUpIv1.setImageResource(R.drawable.bg_hang_up_normal1)
      mBinding.hangUpIv3.setImageResource(R.drawable.bg_hang_up_normal3)
    }
  }

  override fun onBackPressed() {
    finish()
  }
}