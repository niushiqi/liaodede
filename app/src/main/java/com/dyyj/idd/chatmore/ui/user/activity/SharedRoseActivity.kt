package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivitySharedRoseBinding
import com.dyyj.idd.chatmore.viewmodel.SharedRoseViewModel

/**
* author : zwj
* e-mail : none
* time   : 2019/01/14
* desc   : 赠人玫瑰
*/
class SharedRoseActivity : BaseActivity<ActivitySharedRoseBinding, SharedRoseViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SharedRoseActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_shared_rose
    }

    override fun onViewModel(): SharedRoseViewModel {
        return SharedRoseViewModel()
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
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "我的礼物"
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }

        mBinding.vp.adapter = mViewModel.getAdapter(supportFragmentManager)
        mBinding.tl4.setViewPager(mBinding.vp)

    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
    }

}