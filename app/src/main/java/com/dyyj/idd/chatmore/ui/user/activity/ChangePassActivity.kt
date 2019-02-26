package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.app.ChatApp
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityChangePassBinding
import com.dyyj.idd.chatmore.viewmodel.ChangePassViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ChangePassActivity : BaseActivity<ActivityChangePassBinding, ChangePassViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChangePassActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_change_pass
    }

    override fun onViewModel(): ChangePassViewModel {
        return ChangePassViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initView()
    }

    private fun initView() {
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "修改密码"
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }

        mBinding.commitBtn.setOnClickListener {
            if (mBinding.passOldEt.text.toString() == "") {
                Toast.makeText(ChatApp.getInstance().applicationContext, "请输入旧密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mBinding.passNewEt.text.toString() == "") {
                Toast.makeText(ChatApp.getInstance().applicationContext, "请输入新密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mBinding.passNew2Et.text.toString() == "") {
                Toast.makeText(ChatApp.getInstance().applicationContext, "请再次输入新密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mBinding.passNewEt.text.toString() != mBinding.passNew2Et.text.toString()) {
                Toast.makeText(ChatApp.getInstance().applicationContext, "两次新密码输入不相同", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mBinding.passOldEt.text.toString() == mBinding.passNewEt.text.toString()) {
                Toast.makeText(ChatApp.getInstance().applicationContext, "新旧密码不能相同", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mViewModel.netChangeApi(mBinding.passOldEt.text.toString(), mBinding.passNewEt.text.toString())
        }
//        mBinding.passOldEt.text = mDataRepository.getUserInfoEntity()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe
    fun onSubscribeVM(obj: ChangePassViewModel.ChangePassVM) {
        closeProgressDialog()
        if (obj.success) {
            mBinding.passNewEt.setText("")
            mBinding.passNew2Et.setText("")
            mBinding.passOldEt.setText("")
        }
        Toast.makeText(ChatApp.mInstance?.applicationContext, if (obj.success) "密码修改成功" else "密码修改失败" , Toast.LENGTH_SHORT).show()
    }

}