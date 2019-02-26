package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityIdentityBinding
import com.dyyj.idd.chatmore.viewmodel.IdentityViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class IdentityActivity : BaseActivity<ActivityIdentityBinding, IdentityViewModel>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, IdentityActivity::class.java))
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_identity
    }

    override fun onViewModel(): IdentityViewModel {
        return IdentityViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
        initListener()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    private fun initView() {
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "认证"
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }

        mBinding.commitBtn?.setOnClickListener {
            val realName = mBinding.identNameEt.text?:return@setOnClickListener
            val idNO = mBinding.identNumEt.text?:return@setOnClickListener
            if (!personIdValidation(idNO.toString())) {
                return@setOnClickListener
            }
            mViewModel.netIdentityInfo(realName.toString(), idNO.toString())
        }

    }

    /**
     * 检测身份证号正确性
     */
    fun personIdValidation(text: String): Boolean {
        val regx = "[0-9]{17}x"
        val reg1 = "[0-9]{15}"
        val regex = "[0-9]{18}"
        return text.matches(regx.toRegex()) || text.matches(reg1.toRegex()) || text.matches(regex.toRegex())
    }

    @Subscribe
    fun onIdentityVM(obj: IdentityViewModel.IdetityVM) {
        closeProgressDialog()
        if (obj.verify) {
            Toast.makeText(this@IdentityActivity, "认证成功", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this@IdentityActivity, "认证失败", Toast.LENGTH_SHORT).show()
        }
    }
}
