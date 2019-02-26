package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityRedeemCodeBinding
import com.dyyj.idd.chatmore.ui.dialog.activity.ToastActivity
import com.dyyj.idd.chatmore.viewmodel.RedeemCodeViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/24
 * desc   : 输入兑换码
 */
class RedeemCodeActivity : BaseActivity<ActivityRedeemCodeBinding, RedeemCodeViewModel>() {

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, RedeemCodeActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.activity_redeem_code
  }

  override fun onToolbar(): Toolbar? {
    return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
  }

  override fun onViewModel(): RedeemCodeViewModel {
    return RedeemCodeViewModel()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateEvenbus(this)
    initView()
  }

  private fun initView() {
    mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "输入兑换码"
    mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
    mBinding.getAwardBtn.setOnClickListener {
      mViewModel.netUseInviteCode(mBinding.codeEt.text.toString())
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    onDestryEvenbus(this)
  }

  /**
   * 接收邀请码
   */
  @Subscribe()
  fun onInviteCodeVM(obj: RedeemCodeViewModel.InviteCodeVM) {

    if (obj.isSuccess) {
      obj.obj?.let {
        ToastActivity.start(this, rewardCoin = it.coin?.toDouble(), rewardStone = null,
                            rewardCash = it.cash?.toDouble())
      }
    } else {
      niceToast("领取失败")
    }
  }
}