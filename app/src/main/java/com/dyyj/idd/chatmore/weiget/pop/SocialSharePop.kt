package com.dyyj.idd.chatmore.weiget.pop

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.databinding.PopSocialShareBinding
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteGameActivity
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.InviteGameViewModel
import com.dyyj.idd.chatmore.viewmodel.SocialShareViewModel
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

/**
 * Created by wangbin on 2018/12/11.
 * 分享弹窗只负责返回选择的平台号，不负责处理分享逻辑
 * 用法：
 * var mPop = SocialSharePop(mActivity)
 * mPop.initWebListener(mActivity, obj.inviteCode, obj.shareUrl, obj.title, obj.icon)
 * mPop.show(mBinding.root)
 */
class SocialSharePop (context: Context?) : BaseTipPop<PopSocialShareBinding, SocialShareViewModel>(context) {
//class SocialSharePop : BaseTipPop<PopSocialShareBinding, SocialShareViewModel> {
    /*companion object {
        const val TYPE_MIN = 1
        const val TYPE_IMAGE = 2
        const val TYPE_WEB = 3
        const val TYPE_WEB_PACKET = 3
    }

    var shareType: Int = TYPE_MIN

    constructor(context: Context?, shareType: Int) : super(context) {
        this.shareType = shareType
    }*/

    /*var viewModel: ViewModel? = null

    constructor(context: Context?, ): super(context) {
        this.viewModel = viewModel
    }*/

    override fun onLayoutId(): Int {
        return R.layout.pop_social_share
    }

    override fun onViewModel(): SocialShareViewModel {
        return SocialShareViewModel()
    }

    override fun onLayoutSet() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onInitListener() {
        super.onInitListener()
        /*mBinding.qq.setOnClickListener {
            EventBus.getDefault().post((ViewModel.SocialShareVM("qq")))
        }
        mBinding.weibo.setOnClickListener {

        }
        mBinding.weixin.setOnClickListener {
        }*/
        mBinding.pane.setOnClickListener {
            dismiss()
        }
    }

    override fun onInitView() {
        super.onInitView()
        height = ViewGroup.LayoutParams.MATCH_PARENT
        width = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        isTouchable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(R.color.shadow2)!!))
        animationStyle = 0
        backgroundAlpha(context as Activity, 0.5f)
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    private fun backgroundAlpha(context: Activity, bgAlpha: Float) {
        var lp : WindowManager.LayoutParams  = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    override fun dismiss() {
        super.dismiss()
        backgroundAlpha(context as Activity, 1f)
    }

    open fun show(view: View) {
        showAtLocation(view, Gravity.CENTER/* and Gravity.CENTER_HORIZONTAL*/, 0, 0)
    }

    /**
     * 图片分享接口
     */
    open fun initPicListener(context: Activity, bitmap: Bitmap) {
        mBinding.qq.setOnClickListener {
            dismiss()
            ShareUtils.sharePicToQQ(context, bitmap)
        }
        mBinding.weibo.setOnClickListener {
            dismiss()
            ShareUtils.sharePicToWEIBO(context, bitmap)
        }
        mBinding.weixin.setOnClickListener {
            dismiss()
            ShareUtils.sharePicToWEIXIN(context, bitmap)
        }
    }

    /**
     * 小程序分享接口
     */
    open fun initMinListener(context: Activity, inviteCode: String?, title: String?, resources: Resources) {
        mBinding.qq.setOnClickListener {
            dismiss()
        }
        mBinding.weibo.setOnClickListener {
            dismiss()
        }
        mBinding.weixin.setOnClickListener {
            dismiss()
            ShareUtils.shareSmall(context, inviteCode, title, "聊得得小游戏", "/pages/index/index", "gh_4ab55bfec127", BitmapFactory.decodeResource(resources, R.drawable.ic_game_icon))
        }
    }

    /**
     * WEB分享接口
     */
    open fun initWebListener(context: Activity, inviteCode: String?, shareUrl: String?, title: String?, icon: String?) {
        mBinding.qq.setOnClickListener {
            dismiss()
            ShareUtils.shareWebToQQ(context, inviteCode, shareUrl, title, icon, false)
        }
        mBinding.weibo.setOnClickListener {
            dismiss()
            ShareUtils.shareWebToWEIBO(context, inviteCode, shareUrl, title, icon, false)
        }
        mBinding.weixin.setOnClickListener {
            dismiss()
            ShareUtils.shareWebToWEIXIN(context, inviteCode, shareUrl, title, icon, false)
        }
    }

    /**
     * WEB红包分享接口
     */
    open fun initWebPacketListener(context: Activity, inviteCode: String?, shareUrl: String?, title: String?, icon: String?) {
        mBinding.qq.setOnClickListener {
            dismiss()
            ShareUtils.shareWeb3ToQQ(context, inviteCode, shareUrl, title, icon, false)
        }
        mBinding.weibo.setOnClickListener {
            dismiss()
            ShareUtils.shareWeb3ToWEIBO(context, inviteCode, shareUrl, title, icon, false)
        }
        mBinding.weixin.setOnClickListener {
            dismiss()
            ShareUtils.shareWeb3ToWEIXIN(context, inviteCode, shareUrl, title, icon, false)
        }
    }

}