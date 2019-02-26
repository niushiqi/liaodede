package com.dyyj.idd.chatmore.ui.user.activity

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityInviteActionBinding
import com.dyyj.idd.chatmore.viewmodel.InviteActionViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class InviteActionActivity : BaseActivity<ActivityInviteActionBinding, InviteActionViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, InviteActionActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_invite_action
    }

    override fun onViewModel(): InviteActionViewModel {
        return InviteActionViewModel()
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

    private fun initListener() {
        mBinding.ivInviteFriend.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                enterOtherWithPermissionCheck()
            } else {
//                enterOther()
            }
        }
        mBinding.ivCopy.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 将文本内容放到系统剪贴板里。
            cm!!.setText(mBinding.txtInviteCode.text)
            niceToast("邀请码已复制到剪切板")
//
//            ShareUtils.shareWeb(this@InviteActionActivity, false)
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_LOGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SET_DEBUG_APP,
//            Manifest.permission.SYSTEM_ALERT_WINDOW,
//            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WRITE_APN_SETTINGS)
    fun shareWX() {
//        ShareUtils.shareWeb(this@InviteActionActivity, false)
    }

//    /**
//     * 分享微信
//     */
//    private fun sendWXReq() {
//        val api = WXAPIFactory.createWXAPI(this@InviteActionActivity, MainActivity.APP_ID, true)
//        api.registerApp(MainActivity.APP_ID)
//        val webpage = WXWebpageObject()
//        webpage.webpageUrl = "http://www.baidu.com"
//        val msg = WXMediaMessage(webpage)
//        msg.title = "网页标题"
//        msg.description = "网页描述"
//        msg.thumbData = Util.bmpToByteArray(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), true)
//        val req = SendMessageToWX.Req()
//        req.transaction = Util.buildTransaction("webpage")
//        req.message = msg
//        req.scene = SendMessageToWX.Req.WXSceneSession
////        req.scene = SendMessageToWX.Req.WXSceneTimeline
//        api.sendReq(req)
//    }

//    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    fun enterOther() {
//        InviteFriendPop(this@InviteActionActivity).setCode(mViewModel.inviteData?.inviteCode).showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
//    }

    private fun initView() {
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "邀请码"
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mViewModel.netInviteCode()
    }

    @Subscribe
    fun OnInviteVM(obj: InviteActionViewModel.MyInviteVM) {
        if (obj.success) {
            mBinding.txtInviteCode.text = obj.obj?.inviteCode
            mBinding.txtInviteNum.text = "可邀请人数：${(obj.obj?.limitTime?.toInt()?:0) - (obj.obj?.usedTime?.toInt()?:0)}"
        }
    }

}