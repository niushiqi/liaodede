package com.dyyj.idd.chatmore.ui.user.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityMeBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.database.entity.UserInfoEntity
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaSpaceActivity
import com.dyyj.idd.chatmore.ui.task.activity.TaskSystemActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.ClickLevelDialogActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.InviteNewActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.MyGameToolActivity
import com.dyyj.idd.chatmore.ui.wallet.actiivty.WalletActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.BeInviteViewModel
import com.dyyj.idd.chatmore.viewmodel.MeViewModel
import com.dyyj.idd.chatmore.viewmodel.MessageCircleViewModel
import com.dyyj.idd.chatmore.viewmodel.UserInfoViewModel
import com.dyyj.idd.chatmore.weiget.pop.ContactPop
import com.gt.common.gtchat.extension.niceGlide
import com.gt.common.gtchat.extension.niceToast
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/13
 * desc   :
 */
@RuntimePermissions
class MeActivity : BaseActivity<ActivityMeBinding, MeViewModel>() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MeActivity::class.java))
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_me
    }

    override fun onViewModel(): MeViewModel {
        return MeViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initListenter()
        mViewModel.netUserInfo()
        mViewModel.netUseInviteCode()
        mViewModel.netNewMsg()
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "我的"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    private fun initListenter() {
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.editIv.setOnClickListener { UserInfoActivity.start(this@MeActivity) }
        mBinding.layoutToolbar?.findViewById<View>(
                R.id.right_iv)?.setOnClickListener { SettingsActivity.start(this@MeActivity) }
//        mBinding.moneyCl.setOnClickListener { WalletActivity.start(context = this@MeActivity) }
        mBinding.ivIconPig.setOnClickListener {
            WalletActivity.start(context = this@MeActivity)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_wallet",""))
        }
        mBinding.inviteCl.setOnClickListener {
            //      InviteActionActivity.start(this@MeActivity)
//      val neturl = "http://www.liaodede.com:8083/laxin/1.html?domain=${NetURL.HOST}&userId=${mDataRepository.getUserInfoEntity()?.userId}&timestamp=${System.currentTimeMillis().toString().substring(0, 10)}&deviceId=${DeviceUtils.getDeviceID(niceChatContext())}"
//      Log.e("neturl", neturl)
//      H5Activity.start(this@MeActivity, neturl)
            InviteNewActivity.start(this@MeActivity)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_invite",""))
        }
        //道具商城
//        mBinding.shopCl.setOnClickListener { ShopActivity.start(this@MeActivity) }
        mBinding.ivIconShop.setOnClickListener {
            ShopActivity.start(this@MeActivity,0)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_shop",""))
        }

        mBinding.taskCl.setOnClickListener {
            TaskSystemActivity.start(this@MeActivity)
        }

        //输入兑换码
        mBinding.redeemCodeCl.setOnClickListener {
            RedeemCodeActivity.start(this)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_redeemcode",""))
        }
        //邀请我的人
        mBinding.inviteMeCl.setOnClickListener {
            BeInviteActivity.start(this)
        }

        mBinding.ivIconRank.setOnClickListener {
            RankingActivity.start(this)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_rank",""))
        }

        mBinding.contactCodeCl.setOnClickListener {
            startContactWithPermissionCheck()
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_saylove",""))
        }
        mBinding.horizontalProgressBar.setOnClickListener {
            if (mBinding.tvLevelTag.visibility == View.VISIBLE)
                ClickLevelDialogActivity.start(this)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_levelrate",""))
        }
        mBinding.levelTv.setOnClickListener {
            if (mBinding.tvLevelTag.visibility == View.VISIBLE)
                ClickLevelDialogActivity.start(this)
        }
        mBinding.tvLevelTag.setOnClickListener {
            if (mBinding.tvLevelTag.visibility == View.VISIBLE)
                ClickLevelDialogActivity.start(this)
        }
//        mBinding.avatarIv.setOnClickListener {
//            MyCircleActivity.start(this@MeActivity)
//        }
        mBinding.dynamicsMeCl.setOnClickListener {
            //MyDynamicsActivity.start(this@MeActivity)
            PlazaSpaceActivity.start(this@MeActivity, mDataRepository.getUserid(), null)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_mynews",""))
        }
        mBinding.avatarIv.setOnClickListener {
            UserInfoActivity.start(this@MeActivity)
            EventTrackingUtils.joinPoint(EventBeans("ck_myinfo_head",""))
        }
        mBinding.sharedRoseCl.setOnClickListener {
            SharedRoseActivity.start(this@MeActivity)
        }

        mBinding.myGameToolsCl.setOnClickListener {
            MyGameToolActivity.start(this@MeActivity)
        }

        mBinding.guideCl.setOnClickListener {
            NewbieGuideActivity.start(this@MeActivity)
        }

        mBinding.tvLevelGuideTag.setOnClickListener {
            LevelUpgradeTutorialActivity.start(this@MeActivity)
        }
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun startContact() {
        ContactActivity.start(this)
    }

    @Subscribe
    fun OnUserInfoVM(obj: MeViewModel.UserInfoVM) {
        if (obj.success) {
            obj.obj?.let {
                initData(UserInfoEntity.createUserInfoEntity(it))

                //绑定数据
                mBinding.genderTv.text = obj.obj.userBaseInfo.userLevel
                mBinding.horizontalProgressBar.max = obj.obj.userBaseInfo.nextLevelExperience
                mBinding.horizontalProgressBar.progress = obj.obj.userBaseInfo.userExperience
                mBinding.horizontalProgressBar.isShowText = true
                if (obj.obj.userBaseInfo.userExperience >= obj.obj.userBaseInfo.nextLevelExperience) {
                    mBinding.tvLevelTag.visibility = View.VISIBLE
                } else {
                    mBinding.tvLevelTag.visibility = View.GONE
                }
            }
        } else {
            niceToast("数据异常")
        }
    }

    @Subscribe
    fun onEditUserVM(obj: UserInfoViewModel.EditUserVM) {
        closeProgressDialog()
        if (obj.editOk) {
            initData(mDataRepository.getUserInfoEntity()!!)
        }
    }

    @Subscribe
    fun onBeInviteSuccessVM(obj: BeInviteViewModel.BeInviteVM) {
        if (obj.isSuucees) {
            mViewModel.netUseInviteCode()
        }
    }

    /**
     * 我的邀请
     */
    @Subscribe
    fun onUseInviteCodeVM(obj: MeViewModel.UseInviteCodeVM) {
        if (obj.isSuccess) {//data数据为空,显示邀请码
            findViewById<View>(R.id.invite_me_cl)?.visibility = View.VISIBLE
        } else {//数据不为空,不显示邀请码
            findViewById<View>(R.id.invite_me_cl)?.visibility = View.GONE
        }
    }

    @Subscribe
    fun onFinishViewVM(obj: ContactPop.FinishViewVM) {
        finish()
    }


    private fun initData(it: UserInfoEntity?) {
        niceGlide().load(it?.avatar).asBitmap().transform(
                CropCircleTransformation(this)).crossFade().error(R.drawable.bg_circle_black).placeholder(
                R.drawable.bg_circle_black).into(mBinding.avatarIv)
//        val requestOption = RequestOptions().circleCrop()
//        requestOption.error(R.drawable.bg_circle_black).placeholder(R.drawable.bg_circle_black)
//        Glide.with(this).load(it?.avatar).apply(requestOption).into(mBinding.avatarIv)
        mBinding.nicknameTv.text = it?.nickname
        if (TextUtils.equals(it?.auth,
                        "0")) mBinding.presonApproveIv.visibility = View.GONE else mBinding.presonApproveIv.visibility = View.VISIBLE
        if (TextUtils.equals(it?.gender.toString(), "2")) mBinding.genderIv.setImageResource(
                R.drawable.ic_gender_woman_normal) else mBinding.genderIv.setImageResource(
                R.drawable.ic_gender_main_normal)
        mBinding.ageTv.text = it?.age.toString() + "岁"
        mBinding.levelTv.text = "LV " + it?.userLevel
    }

    @Subscribe
    fun onClearUmMsg(obj: MessageCircleViewModel.ClearUnMsgVM) {
        if (obj.success) {
            mBinding.ivMeNew.visibility = View.GONE
        }
    }

    /**
     * 好友未读消息
     */
    @Subscribe
    fun onNewMsg(obj: MeViewModel.MeUnReadMsgVM) {
        if (obj.success) {
            if (obj.obj.unReadCount?:0 > 0) {
                mBinding.ivMeNew.visibility = View.VISIBLE
                mBinding.ivMeNew.text = obj.obj.unReadCount.toString()
            }
        }
    }

    /**
     * 广场未读消息
     */
    @Subscribe
    fun onSubscribeUnMsg(obj: MeViewModel.SquareUnreadMessageVM) {
        if (obj.success) {
            if (obj.obj > 0) {
                mBinding.ivMeNew.visibility = View.VISIBLE
                mBinding.ivMeNew.text = (obj.obj + mBinding.ivMeNew.text.toString().toInt()).toString()
            }
        }
    }
}