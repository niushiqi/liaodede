package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.FragmentApplyBinding
import com.dyyj.idd.chatmore.model.network.result.ApplyFriendResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.viewmodel.ApplyViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

class ApplyActivity : BaseActivity<FragmentApplyBinding, ApplyViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ApplyActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_apply
    }

    override fun onViewModel(): ApplyViewModel {
        return ApplyViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "好友申请"
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mViewModel.netApplyFriendList()
    }

    fun initRecycleView(list: List<ApplyFriendResult.ApplyFriend>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(this).color(
                ContextCompat.getColor(this, R.color.divider_home)).sizeResId(
                R.dimen.item_decoration_2px).build()
        val lastNums = PreferenceUtil.getInt("lastnum", 0)
        if (lastNums < list.size) {
            for (n in 0..(list.size - lastNums - 1)) {
                list[n].isNew = true
            }
        }
        PreferenceUtil.commitInt("lastnum", list.size)
        mViewModel.getAdapter().initData(list)
        mBinding.recyclerview.addItemDecoration(decoration)
        mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.recyclerview.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
    }

    @Subscribe
    fun OnApplyListVM(obj: ApplyViewModel.ApplyListVM) {
        if (obj.error) {
            obj.data?.requestList?.let {
                initRecycleView(obj.data.requestList)
            }
        }
    }

    @Subscribe
    fun onRefreshFriend(obj: ApplyViewModel.RefreshData) {
        mViewModel.netApplyFriendList()
    }
}