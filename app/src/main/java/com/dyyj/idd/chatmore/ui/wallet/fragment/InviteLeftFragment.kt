package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentInviteLeftBinding
import com.dyyj.idd.chatmore.ui.user.activity.InviteNoticeActivity
import com.dyyj.idd.chatmore.utils.ShareUtils
import com.dyyj.idd.chatmore.viewmodel.InviteLeftViewModel
import com.gt.common.gtchat.extension.niceChatContext
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe
import android.R.attr.thumb
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.weiget.pop.SocialSharePop
import com.umeng.socialize.ShareAction
import com.umeng.socialize.media.UMWeb



class InviteLeftFragment : BaseFragment<FragmentInviteLeftBinding, InviteLeftViewModel>() {



    override fun onLayoutId(): Int {
        return R.layout.fragment_invite_left
    }

    override fun onViewModel(): InviteLeftViewModel {
        return InviteLeftViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        mBinding.textView37.setOnClickListener {
            //showProgressDialog()
            mViewModel.netShareMessage()
            EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite1_share",""))
        }
        mBinding.txtShuoming.setOnClickListener {
            InviteNoticeActivity.start(mActivity, mBinding.inviteCode.text.toString())
            EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite1_how",""))
        }
        mViewModel.netInviteCode()
    }

    @Subscribe
    fun onShareMessageVM(obj: InviteLeftViewModel.ShareMessageVM) {
        //closeProgressDialog()
        if (obj.success) {
            var mPop = SocialSharePop(mActivity)
            mPop.initWebListener(mActivity, obj.inviteCode, obj.shareUrl, obj.title, obj.icon)
            mPop.show(mBinding.root)
            //ShareUtils.shareWeb(mActivity!!, obj.inviteCode, obj.shareUrl, obj.title, obj.icon, false)
            //ShareUtils.init(mActivity,obj.shareUrl,obj.title,obj.icon)
        }
    }

    @Subscribe
    fun onInviteCodeVM(obj: InviteLeftViewModel.InviteCodeVM) {
        mBinding.inviteCode.text = obj.code
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateEvenBus(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        onDestryEvenBus(this)
        super.onDestroyView()
    }

}