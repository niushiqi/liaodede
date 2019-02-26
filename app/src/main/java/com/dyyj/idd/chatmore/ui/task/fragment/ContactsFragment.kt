package com.dyyj.idd.chatmore.ui.task.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentContactsBinding
import com.dyyj.idd.chatmore.eventtracking.EventConstant
import com.dyyj.idd.chatmore.eventtracking.EventMessage
import com.dyyj.idd.chatmore.model.network.result.ContactsResult
import com.dyyj.idd.chatmore.ui.user.activity.ApplyActivity
import com.dyyj.idd.chatmore.viewmodel.ApplyViewModel
import com.dyyj.idd.chatmore.viewmodel.ChatViewModel
import com.dyyj.idd.chatmore.viewmodel.ContactsViewModel
import com.gt.common.gtchat.extension.niceToast
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 好友列表
 */
class ContactsFragment : BaseFragment<FragmentContactsBinding, ContactsViewModel>() {
    override fun onLayoutId(): Int {
        return R.layout.fragment_contacts
    }

    override fun onViewModel(): ContactsViewModel {
        return ContactsViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        mBinding.tvApplyNum.setOnClickListener { ApplyActivity.start(mActivity) }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.netContactsList()
    }

    @Subscribe
    fun onRefreshFriend(obj: ApplyViewModel.RefreshFriend) {
        mViewModel.netContactsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestryEvenBus(this)
    }

    private fun initRecycleView(list: List<ContactsResult.Data.Friends>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
                ContextCompat.getColor(activity!!, R.color.divider_home)).sizeResId(
                R.dimen.item_decoration_2px).build()
        mViewModel.getAdapter().initData(list)
        mBinding.recyclerview.addItemDecoration(decoration)
        mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.recyclerview.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
    }

    @Subscribe
    fun onContactsVM(obj: ContactsViewModel.ContactsVM) {
        if (obj.isSuccess) {
            obj.obj?.data?.friendsList?.let {
                mViewModel.saveContactsList(obj.obj)
                initRecycleView(it)
                if (it.isEmpty()) {
                    mBinding.clEmpty.visibility = View.VISIBLE
                } else {
                    mBinding.clEmpty.visibility = View.GONE
                }
            }
        } else {
            mActivity.niceToast("获取联系人列表")
        }
    }


    /**
     * 解除好友关系
     */
    @Subscribe
    fun onRevokeFriendshipVM(obj: ChatViewModel.RevokeFriendshipVM) {
        if (obj.isSuccess) {
            mViewModel.netContactsList()
        }
    }

    @Subscribe
    fun onEventMessage(msg: EventMessage<Any>) {
        if (msg.tag.equals(EventConstant.TAG.TAG_MESSAGE_UNREAD_COUNT)){
            when(msg.what){
                EventConstant.WHAT.WHAT_UNREAD_FRIEND_COUNT -> {
                    var count: Int = msg.obj as Int
                    if (count > 0){
                        mBinding.tvApplyNum.text = "${count}条好友申请"
                        mBinding.tvApplyNum.visibility = View.VISIBLE
                        mBinding.clEmpty.visibility = View.GONE
                    }else{
                        mBinding.tvApplyNum.visibility = View.GONE
                        //mViewModel.netContactsList()
                    }
                }
            }
        }
    }

}