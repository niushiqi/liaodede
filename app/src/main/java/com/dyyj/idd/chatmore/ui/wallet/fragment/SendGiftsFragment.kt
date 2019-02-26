package com.dyyj.idd.chatmore.ui.wallet.fragment

import android.support.v7.widget.LinearLayoutManager
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentSharedRoseBinding
import com.dyyj.idd.chatmore.ui.adapter.ShareRoseAdapter
import com.dyyj.idd.chatmore.viewmodel.SendGiftsViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * e-mail : none
 * time   : 2019/01/18
 * desc   : 礼物
 */
class SendGiftsFragment : BaseFragment<FragmentSharedRoseBinding, SendGiftsViewModel>() {

    override fun onLayoutId(): Int {
        return R.layout.fragment_shared_rose
    }

    override fun onViewModel(): SendGiftsViewModel {
        return SendGiftsViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        initData()
        mViewModel.netMySharedRose()
        initListener()
    }

    private fun initData() {
      mBinding.rvShareRose.layoutManager = LinearLayoutManager(mActivity)
      mViewModel.netMySharedRose()
    }

    private fun initListener() {
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestryEvenBus(this)
    }

    @Subscribe
    fun OnSubscribeVM(obj: SendGiftsViewModel.MySharedRoseVM) {
        mBinding.rvShareRose.adapter = ShareRoseAdapter(mActivity, obj.obj)
    }

}