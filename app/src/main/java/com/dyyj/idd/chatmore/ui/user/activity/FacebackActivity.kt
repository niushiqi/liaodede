package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityFacebackBinding
import com.dyyj.idd.chatmore.viewmodel.FacebackViewModel
import com.gt.common.gtchat.extension.niceToast

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/22
 * desc   : 意见反馈
 */
class FacebackActivity:BaseActivity<ActivityFacebackBinding, FacebackViewModel>() {
  companion object {
    fun start(context: Context) {
      val intent = Intent(context, FacebackActivity::class.java)
      context.startActivity(intent)
    }
  }
  override fun onLayoutId(): Int {
    return R.layout.activity_faceback
  }

  override fun onViewModel(): FacebackViewModel {
    return FacebackViewModel()
  }

  override fun onToolbar(): Toolbar? {
    return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initView()
    initListener()

  }

  private fun initView() {
    mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "意见反馈"
  }

  private fun initListener() {
    mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
    mBinding.commitBtn.setOnClickListener {
      niceToast("提交成功", 3000)
    }
  }
}