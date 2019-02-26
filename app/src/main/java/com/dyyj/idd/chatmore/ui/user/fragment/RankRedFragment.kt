package com.dyyj.idd.chatmore.ui.user.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentRankRedBinding
import com.dyyj.idd.chatmore.model.network.result.RedTopData
import com.dyyj.idd.chatmore.viewmodel.RankRedViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

class RankRedFragment : BaseFragment<FragmentRankRedBinding, RankRedViewModel>() {

    private var PAGE = 1
    private val PAGE_SIZE = 10
    private var lastVisibleItem = 0

    override fun onLayoutId(): Int {
        return R.layout.fragment_rank_red
    }

    override fun onViewModel(): RankRedViewModel {
        return RankRedViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        onCreateEvenBus(this)
        initRecycleView()
        mViewModel.netRankList(PAGE, PAGE_SIZE)
        mBinding.swipeLayout.setOnRefreshListener {
            PAGE = 1
            mViewModel.netRankList(PAGE, PAGE_SIZE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestryEvenBus(this)
    }

    private fun initRecycleView() {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(activity).color(
                ContextCompat.getColor(activity!!, R.color.divider_home)).sizeResId(
                R.dimen.item_decoration_2px).build()
//        mViewModel.getAdapter().initData(list)
        val head = LayoutInflater.from(context).inflate(R.layout.item_head_red, null)
        head.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        mBinding.rvList.addHeaderView(head)
        mBinding.rvList.addItemDecoration(decoration)
        mBinding.rvList.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvList.layoutManager = LinearLayoutManager(activity)
        mBinding.rvList.adapter = mViewModel.getAdapter()
        mViewModel.getAdapter().mRealScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //最后一个可见的ITEM
                lastVisibleItem = (mBinding.rvList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mViewModel.getAdapter().itemCount) {
                    PAGE += 1
                    mViewModel.netRankList(PAGE, PAGE_SIZE)
                }
            }
        }
    }

    @Subscribe
    fun OnSubscribRed(vm: RankRedViewModel.RankRedVM) {
        mBinding.swipeLayout.isRefreshing = false
        if (vm.isSuccess) {
//            initRecycleView(vm.obj?.data!!)
            if (vm.more) {
                mViewModel.getAdapter().moreData(vm.obj?.data!!)
            } else {
                initBottom(vm.obj?.userInfo)
                mViewModel.getAdapter().refreshData(vm.obj?.data!!)
            }
        }
    }

    private fun initBottom(userInfo: RedTopData.RedUserInfo?) {
        mBinding.txtBottomText1.text = userInfo?.nickName
        mBinding.txtBottomText2.text = "红包 ${userInfo?.opendRedEnvelopeNum}个"
        mBinding.txtBottomText3.text = "${userInfo?.userCash}元"
        mBinding.txtBottomText4.text = "${userInfo?.userCoin}"
    }

}