package com.dyyj.idd.chatmore.ui.user.fragment

import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentMessageCircleBinding
import com.dyyj.idd.chatmore.viewmodel.MessageCircleViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * Created by wangbin on 2018/12/26.
 */

class MessageCircleFragment: BaseFragment<FragmentMessageCircleBinding, MessageCircleViewModel>() {

    var lastVisibleItem = 0
    var PAGE = 1
    var loadMore = true
    var loading = false
    companion object {
        const val PAGE_SIZE = 10
        const val REQUEST_CODE_1 = 100
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_message_circle
    }

    override fun onViewModel(): MessageCircleViewModel {
        return MessageCircleViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        initRecycleView()
        mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, MessageCircleFragment.PAGE_SIZE)
        mViewModel.netUpdateMsg()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestryEvenBus(this)
    }

    private fun initRecycleView() {
        mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.recyclerview.layoutManager = LinearLayoutManager(mActivity)
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
//        mViewModel.getAdapter().mRealScrollListener = object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                //最后一个可见的ITEM
//                lastVisibleItem = (mBinding.recyclerview.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mViewModel.getAdapter().itemCount) {
//                    if (loadMore and !loading) {
//                        PAGE += 1
//                        loading = true
//                        mViewModel.netPlazaCardList(mDataRepository.getUserid()!!, PAGE, MyCircleActivity.PAGE_SIZE)
//                    }
//                }
//            }
//        }
    }

    @Subscribe
    fun onSubscribeCircle(obj: MessageCircleViewModel.MyCircleMsgVM) {
        loading = false
        if (obj.success) {
            if (obj.more) {
                mViewModel.getAdapter().moreData(obj?.vm!!)
                if (mViewModel.getAdapter().getList().size < PAGE * MessageCircleFragment.PAGE_SIZE) {
                    loadMore = false
                }
            } else {
                loadMore = true
                mViewModel.getAdapter().refreshData(obj?.vm!!)
            }
        } else {
            mActivity.niceToast(obj.message ?: "", Toast.LENGTH_SHORT)
        }
    }

}