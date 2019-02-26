package com.dyyj.idd.chatmore.ui.task.fragment

import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentApplyBinding
import com.dyyj.idd.chatmore.model.network.result.ApplyFriendResult
import com.dyyj.idd.chatmore.model.preferences.PreferenceUtil
import com.dyyj.idd.chatmore.viewmodel.ApplyViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/06/30
 * desc   : 申请列表
 */
class ApplyFragment : BaseFragment<FragmentApplyBinding, ApplyViewModel>() {
    override fun onLayoutId(): Int {
        return R.layout.fragment_apply
    }

    override fun onViewModel(): ApplyViewModel {
        return ApplyViewModel()
    }

//    override fun lazyLoad() {
//        super.lazyLoad()
//        onCreateEvenBus(this)
//        mViewModel.netApplyFriendList()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        onDestryEvenBus(this)
//    }

    fun initRecycleView(list: List<ApplyFriendResult.ApplyFriend>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
                ContextCompat.getColor(activity!!, R.color.divider_home)).sizeResId(
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
        mBinding.recyclerview.layoutManager = LinearLayoutManager(activity)
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
    }

//    @Subscribe
//    fun OnApplyListVM(obj: ApplyViewModel.ApplyListVM) {
//        if (obj.error) {
//            obj.data?.requestList?.let {
//                initRecycleView(obj.data.requestList)
//            }
//        }
//    }

}