package com.dyyj.idd.chatmore.ui.user.activity

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityInviteNoticeBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.InviteNoticeViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

class InviteNoticeActivity: BaseActivity<ActivityInviteNoticeBinding, InviteNoticeViewModel>() {
    companion object {
        val INVITECODE = "invitecode"
        fun start(context: Context, code: String) {
            val intent = Intent(context, InviteNoticeActivity::class.java)
            intent.putExtra(INVITECODE, code)
            context.startActivity(intent)
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.activity_invite_notice
    }

    override fun onViewModel(): InviteNoticeViewModel {
        return InviteNoticeViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "邀请好友奖励"
        mBinding.txtInviteCode.text = intent.getStringExtra(INVITECODE)
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
        }
    }

    @Subscribe
    fun onShareMessageVM(obj: InviteNoticeViewModel.ShareMessageVM) {
        closeProgressDialog()
        if (obj.success) {
            ShareUtils.shareWebToWEIXIN(this, obj.inviteCode, obj.shareUrl, obj.title, obj.icon, false)
        }
    }
}