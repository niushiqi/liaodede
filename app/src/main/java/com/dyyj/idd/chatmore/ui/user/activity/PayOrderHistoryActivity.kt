package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityWithdrawRecordBinding
import com.dyyj.idd.chatmore.ui.adapter.PayHistoryAdapter
import com.dyyj.idd.chatmore.viewmodel.WithdrawRecordViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : malibo
 * time   : 2018/10/07
 * desc   :
 */
class PayOrderHistoryActivity: BaseActivity<ActivityWithdrawRecordBinding, WithdrawRecordViewModel>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, PayOrderHistoryActivity::class.java))
        }
    }
    override fun onViewModel(): WithdrawRecordViewModel {
        return WithdrawRecordViewModel()
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_withdraw_record
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initView()
        mViewModel.netPayHistory()

    }

    private fun initView() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "支付记录"

        //创建布局管理
        val layoutManager = LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerview?.setLayoutManager(layoutManager);


    }

    @Subscribe
    fun OnSubscribeVM(obj: WithdrawRecordViewModel.PayHistoryVM) {
        var data = obj.obj
        val myadapter = PayHistoryAdapter(data)

        mBinding.recyclerview?.setAdapter(myadapter)
    }
}