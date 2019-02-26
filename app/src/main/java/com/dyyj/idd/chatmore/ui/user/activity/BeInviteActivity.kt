package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityBeInviteBinding
import com.dyyj.idd.chatmore.viewmodel.BeInviteViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/08/25
 * desc   :邀请我的人
 */
class BeInviteActivity : BaseActivity<ActivityBeInviteBinding, BeInviteViewModel>() {

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, BeInviteActivity::class.java)
      context.startActivity(intent)
    }
  }

  override fun onLayoutId(): Int {
    return R.layout.activity_be_invite
  }

  override fun onViewModel(): BeInviteViewModel {
    return BeInviteViewModel()
  }

  override fun onToolbar(): Toolbar? {
    return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    onCreateEvenbus(this)
    initView()
  }

  override fun onDestroy() {
    super.onDestroy()
    onDestryEvenbus(this)
  }

  private fun initView() {
    mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "邀请我的人"
    mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
    mBinding.getAwardBtn.setOnClickListener {
      mViewModel.netRegVerifyCode(mBinding.codeEt.text.toString())
    }
  }

  /**
   * 接收邀请码
   */
  @Subscribe
  fun onBeInviteVM(obj: BeInviteViewModel.BeInviteVM) {
    if (obj.isSuucees) {
      niceToast("邀请验证成功，已成为好友")
      finish()
    } else {
      niceToast("邀请验证失败")
    }
  }
}