package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityAboutBinding
import com.dyyj.idd.chatmore.viewmodel.AboutViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/21
 * desc   :
 */
class AboutActivity : BaseActivity<ActivityAboutBinding, AboutViewModel>() {

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, AboutActivity::class.java)
      context.startActivity(intent)

    }
  }
  override fun onLayoutId(): Int {
    return R.layout.activity_about
  }

  override fun onViewModel(): AboutViewModel {
    return AboutViewModel()
  }

  override fun onToolbar(): Toolbar? {
    return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "关于"
    mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
    mBinding.versionTv.text = getVersionName()
    mBinding.versionBtn.setOnClickListener {  }
  }

  @Throws(Exception::class)
  private fun getVersionName(): String {
    // 获取packagemanager的实例
    val packageManager = packageManager
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    val packInfo = packageManager.getPackageInfo(packageName, 0)
    return "V"+packInfo.versionName
  }
}