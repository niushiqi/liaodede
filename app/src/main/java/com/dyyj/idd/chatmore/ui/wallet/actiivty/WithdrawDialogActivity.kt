package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.DialogTixianBinding
import com.dyyj.idd.chatmore.ui.user.activity.WithdrawRecordActivity
import org.greenrobot.eventbus.EventBus
import com.dyyj.idd.chatmore.viewmodel.DialogTixianViewModel


@Suppress("UNREACHABLE_CODE")
/**
 * author : malibo
 * time   : 2018/10/04
 * desc   : 提现成功后的dialog
 */
class WithdrawDialogActivity : BaseActivity<DialogTixianBinding, DialogTixianViewModel>() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, WithdrawDialogActivity::class.java);
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.dialog_tixian
    }

    override fun onViewModel(): DialogTixianViewModel {
        return DialogTixianViewModel()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @SuppressLint("WrongViewCast")
    private fun initView() {
        mBinding.tvFindRecord.setOnClickListener{
            WithdrawRecordActivity.start(this)
            finish()
        }
        mBinding.okBtn.setOnClickListener{finish()}
    }

}