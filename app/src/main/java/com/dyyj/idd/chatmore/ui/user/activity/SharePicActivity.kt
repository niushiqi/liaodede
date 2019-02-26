package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivitySharePicBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.SharePicViewModel
import com.dyyj.idd.chatmore.weiget.pop.SocialSharePop
import com.umeng.socialize.UMShareAPI
import org.greenrobot.eventbus.Subscribe

class SharePicActivity: BaseActivity<ActivitySharePicBinding, SharePicViewModel>() {
    companion object {
        val INVITECODE = "invitecode"
        fun start(context: Context, code: String) {
            val intent = Intent(context, SharePicActivity::class.java)
            intent.putExtra(INVITECODE, code)
            context.startActivity(intent)
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.activity_share_pic
    }

    override fun onViewModel(): SharePicViewModel {
        return SharePicViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "分享"
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.txtInviteCode.text = intent.getStringExtra(InviteNoticeActivity.INVITECODE)
        mBinding.clImage.isDrawingCacheEnabled = true

        mBinding.rlShare.setOnClickListener {
//            showProgressDialog()
//            mViewModel.netShareMessage()
            //ShareUtils.sharePic(this, mBinding.clImage.getDrawingCache(true))
            var mPop = SocialSharePop(this@SharePicActivity)
            mPop.initPicListener(this@SharePicActivity, mBinding.clImage.getDrawingCache(true))
            mPop.show(mBinding.root)
            EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite1_how_send",""))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe
    fun onShareMessageVM(obj: SharePicViewModel.ShareMessageVM) {
        closeProgressDialog()
        if (obj.success) {
            ShareUtils.shareWebToWEIXIN(this, obj.inviteCode, obj.shareUrl, obj.title, obj.icon, false)
        }
    }
}