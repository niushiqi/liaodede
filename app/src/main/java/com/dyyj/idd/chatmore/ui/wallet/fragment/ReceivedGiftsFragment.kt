package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.support.v7.widget.LinearLayoutManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentReceivedRoseBinding
import com.dyyj.idd.chatmore.ui.adapter.ReceivedRoseAdapter
import com.dyyj.idd.chatmore.viewmodel.ReceivedGiftsViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/18
 * desc   : 礼物
 */
class ReceivedGiftsFragment : BaseFragment<FragmentReceivedRoseBinding, ReceivedGiftsViewModel>() {

    override fun onLayoutId(): Int {
        return R.layout.fragment_received_rose
    }

    override fun onViewModel(): ReceivedGiftsViewModel {
        return ReceivedGiftsViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        initData()
        initListener()
    }

    private fun initData() {
        mBinding.rvReceivedRose.layoutManager = LinearLayoutManager(mActivity)
        mViewModel.netMySharedRose()
    }

    private fun initListener() {
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestryEvenBus(this)
    }

    @Subscribe
    fun OnSubscribeVM(obj: ReceivedGiftsViewModel.MySharedRoseVM) {
        mBinding.rvReceivedRose.adapter = ReceivedRoseAdapter(mActivity, obj.obj)
    }
}