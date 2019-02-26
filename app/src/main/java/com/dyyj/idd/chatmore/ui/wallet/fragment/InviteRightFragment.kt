package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentInviteRightBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.model.network.result.MyTaskResult
import com.dyyj.idd.chatmore.ui.adapter.InviteCenterAdapter
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.InviteRightViewModel
import com.dyyj.idd.chatmore.weiget.pop.SocialSharePop
import com.dyyj.idd.chatmore.weiget.pop.TalkFriendPop
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

class InviteRightFragment: BaseFragment<FragmentInviteRightBinding, InviteRightViewModel>() {
    override fun onLayoutId(): Int {
        return R.layout.fragment_invite_right
    }

    override fun onViewModel(): InviteRightViewModel {
        return InviteRightViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initListener()
        initView()
        mViewModel.netMyTasks()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateEvenBus(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        onDestryEvenBus(this)
        super.onDestroyView()
    }

    private fun initView() {
    }

    private fun initListener() {
        mBinding.textView37.setOnClickListener {
            showProgressDialog()
            mViewModel.netShareMessage()
//            ShareUtils.shareWeb(mActivity, "http://www.baidu.com", "title", "des", true)
            EventTrackingUtils.joinPoint(EventBeans("ck_invitepage_invite1_share",""))
        }
    }

    @Subscribe
    fun onShareMessageVM(obj: InviteRightViewModel.ShareMessageVM) {
        closeProgressDialog()
        if (obj.success) {
//            Glide.with(activity).load("http://c.hiphotos.baidu.com/image/pic/item/9d82d158ccbf6c81924a92c5b13eb13533fa4099.jpg").asBitmap().crossFade().listener(object : RequestListener<String, Bitmap> {
//                override fun onException(e: Exception?, model: String?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
//                    return false
//                }
//
//                override fun onResourceReady(resource: Bitmap?, model: String?, target: Target<Bitmap>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
//                    ShareUtils.shareWeb(mActivity, obj.shareUrl, obj.title, resource, true)
//                    return false
//                }
//            })
            var mPop = SocialSharePop(mActivity)
            mPop.initWebListener(mActivity, obj.inviteCode, obj.shareUrl, obj.title, obj.icon)
            mPop.show(mBinding.root)
            //ShareUtils.shareWeb(mActivity, obj.inviteCode, obj.shareUrl, obj.title, obj.icon, false)
        }
    }

    fun initRecycleView(list: List<MyTaskResult.Data.MyTask>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
                ContextCompat.getColor(activity!!, android.R.color.transparent)).sizeResId(
                R.dimen.item_decoration_2px).build()
        mViewModel.getAdapter().initData(list)
        mBinding.rvCenter.addItemDecoration(decoration)
        mBinding.rvCenter.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvCenter.layoutManager = LinearLayoutManager(activity)
        mBinding.rvCenter.adapter = mViewModel.getAdapter()

    }

    @Subscribe
    fun onMyTasksVM(obj: InviteRightViewModel.MyTasksVM) {
        if (obj.success) {
            initRecycleView(obj.obj?.myTasks!!)
        }
    }

    @Subscribe
    fun showPopVM(obj: InviteCenterAdapter.ShowInvitePopVM) {
        val showPop = TalkFriendPop(mActivity)
        showPop.setData(obj.obj)
        showPop.showAtLocation(mBinding.root, Gravity.CENTER, 0, 0)
    }

    @Subscribe
    fun showFriendWxVM(obj: InviteCenterAdapter.ShowMessageToFriendVM) {
//        ShareUtils.shareWeb(mActivity, "http://www.baidu.com", "title", "des", false)
        showProgressDialog()
        mViewModel.netShareMessage()
    }
}