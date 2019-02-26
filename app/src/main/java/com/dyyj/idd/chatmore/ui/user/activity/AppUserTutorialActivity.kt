package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityAppUserTutorialBinding
import com.dyyj.idd.chatmore.viewmodel.AppUserTutorialViewModel

/**
 * Created by wangbin on 2018/12/12.
 */
class AppUserTutorialActivity: BaseActivity<ActivityAppUserTutorialBinding, AppUserTutorialViewModel>() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AppUserTutorialActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_app_user_tutorial
    }

    override fun onViewModel(): AppUserTutorialViewModel {
        return AppUserTutorialViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "聊得得怎么玩"
        /*mBinding.txtInviteCode.text = intent.getStringExtra(INVITECODE)
        mBinding.rlInvote.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm!!.setText(mBinding.txtInviteCode.text)
            niceToast("邀请码已复制到剪切板")
            EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite1_how_copy",""))
        }
        mBinding.txtShare.setOnClickListener {
            //            showProgressDialog()
//            mViewModel.netShareMessage()
            SharePicActivity.start(this, intent.getStringExtra(INVITECODE))
            EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite1_how_share",""))
        }*/
    }
}