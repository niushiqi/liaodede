package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityMakeMoneyTutorialBinding
import com.dyyj.idd.chatmore.viewmodel.MakeMoneyTutorialViewModel

/**
 * Created by wangbin on 2018/12/12.
 */
class MakeMoneyTutorialActivity: BaseActivity<ActivityMakeMoneyTutorialBinding, MakeMoneyTutorialViewModel>() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MakeMoneyTutorialActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_make_money_tutorial
    }

    override fun onViewModel(): MakeMoneyTutorialViewModel {
        return MakeMoneyTutorialViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "聊得得怎么赚钱"
    }
}
