package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivitySettingsBinding
import com.dyyj.idd.chatmore.utils.ActManagerUtils
import com.dyyj.idd.chatmore.viewmodel.SettingsViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/21
 * desc   : 设置
 */
class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingsViewModel>() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_settings
    }

    override fun onViewModel(): SettingsViewModel {
        return SettingsViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "设置"
        initListener()

    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.passCl.setOnClickListener { ChangePassActivity.start(this@SettingsActivity) }
        mBinding.facebackCl.setOnClickListener { FacebackActivity.start(this@SettingsActivity) }
        mBinding.gradeCl.setOnClickListener { }
        mBinding.aboutCl.setOnClickListener { AboutActivity.start(this@SettingsActivity) }
        mBinding.btnLogout.setOnClickListener {
            MaterialDialog.Builder(this@SettingsActivity).content(
                    "确定要退出?").positiveText("确定").negativeText(
                    "取消").onPositive { dialog, which ->
                startLoginActivity()

            }.show()


        }
    }

    private fun startLoginActivity() {
        ActManagerUtils.getAppManager().finishAllActivity()
        ActManagerUtils.getAppManager().finishAllActivityVisible()
        LoginActivity.start(this)
        mViewModel.cleanAccount(this)
        mDataRepository.saveLoginToken("")
    }
}