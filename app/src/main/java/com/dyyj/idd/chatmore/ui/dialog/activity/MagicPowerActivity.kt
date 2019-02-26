package com.dyyj.idd.chatmore.ui.dialog.activity

import android.content.Context
import android.content.Intent
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.DialogMagicPowerBinding
import com.dyyj.idd.chatmore.viewmodel.MagicPowerViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/14
 * desc   : 魔力不足提示框
 */
class MagicPowerActivity:BaseActivityV2<DialogMagicPowerBinding, MagicPowerViewModel>() {

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, MagicPowerActivity::class.java)
      context.startActivity(intent)
    }
  }
  override fun onLayoutId(): Int {
    return R.layout.dialog_magic_power
  }

  override fun onViewModel(): MagicPowerViewModel {
    return MagicPowerViewModel()
  }
}