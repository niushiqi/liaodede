package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityCashNoticeBinding
import com.dyyj.idd.chatmore.viewmodel.CashNoticeViewModel

class CashNoticeActivity: BaseActivity<ActivityCashNoticeBinding, CashNoticeViewModel>() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CashNoticeActivity::class.java))
        }
    }
    override fun onViewModel(): CashNoticeViewModel {
        return CashNoticeViewModel()
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_cash_notice
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "提现说明"
    }
}