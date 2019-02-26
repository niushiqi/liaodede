package com.dyyj.idd.chatmore.ui.dialog.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivityV2
import com.dyyj.idd.chatmore.databinding.ActivityWithdrawDialogBinding
import com.dyyj.idd.chatmore.viewmodel.WithdrawDialogViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/16
 * desc   : 提现成功dialog
 */
class WithdrawDialogActivity:BaseActivityV2<ActivityWithdrawDialogBinding, WithdrawDialogViewModel>() {

  companion object {
    fun start(context: Context) {
      context.startActivity(Intent(context, WithdrawDialogActivity::class.java))
    }
  }
  override fun onLayoutId(): Int {
    return R.layout.activity_withdraw_dialog
  }

  override fun onViewModel(): WithdrawDialogViewModel {
    return WithdrawDialogViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initView()
  }

  private fun initView() {
    mBinding.taskBtn.setOnClickListener { onBackPressed() }
  }

  override fun onBackPressed() {
    finish()
  }
}