package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaListBinding
import com.dyyj.idd.chatmore.viewmodel.PlazaSpaceCardsViewModel
import com.gt.common.gtchat.extension.niceToast

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 我的空间 动态  (展示自己在广场和好友圈里发的动态)
 */
class PlazaSpaceCardsFragment : BaseFragment<FragmentPlazaListBinding, PlazaSpaceCardsViewModel>() {
    companion object {

        fun create(userID: String): PlazaSpaceCardsFragment {
            val fragment = PlazaSpaceCardsFragment()
            val bundle = Bundle()
            bundle.putString("KEY_ID", userID)
            fragment.arguments = bundle
            return fragment
        }
    }

    var lastVisibleItem = 0
    var PAGE = 1
    var PAGE_SIZE = 10
    var loading = false

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza_list
    }

    override fun onViewModel(): PlazaSpaceCardsViewModel {
        return PlazaSpaceCardsViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initRecycleView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        onCreateEvenBus(this)

        mViewModel.usetID = arguments!!.getString("KEY_ID")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        netPlazaCardList(PAGE, PAGE_SIZE)
    }

    override fun onDestroyView() {
//        onDestryEvenBus(this)
        super.onDestroyView()
    }

    private fun initRecycleView() {
        mBinding.swipeLayout.setOnRefreshListener {
            PAGE = 1
            loading = true
            netPlazaCardList(PAGE, PAGE_SIZE)
        }

        loading = true
        netPlazaCardList(PAGE, PAGE_SIZE)
        mBinding.content.layoutManager = LinearLayoutManager(activity)
        mBinding.content.adapter = mViewModel.getAdapter()
        mBinding.content.setHasFixedSize(true)

        mBinding.content.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mViewModel.getAdapter().mRealScrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //最后一个可见的ITEM
                lastVisibleItem = (mBinding.content.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && !loading
                        && lastVisibleItem + 1 == mViewModel.getAdapter().itemCount) {
                    PAGE += 1
                    loading = true
                    netPlazaCardList(PAGE, PAGE_SIZE)
                }
            }
        }
    }

    fun netPlazaCardList(page: Int, pageSize: Int) {
        val subscribe = mDataRepository.myDynamicList(mViewModel.usetID, page.toString(), pageSize.toString())
                .subscribe({
                    loading = false
                    mBinding.swipeLayout.isRefreshing = false

                    val adapter = mViewModel.getAdapter()
                    val list = it.data?.list ?: listOf()

                    if (list.isNotEmpty()) {
                        mBinding.iv.visibility = View.GONE
                        mBinding.tv.visibility = View.GONE
                    }

                    if (page != 1) {
                        adapter.moreData(list)
                        adapter.isLoadMore = adapter.getList().size >= PAGE * PAGE_SIZE

                    } else {
                        adapter.isLoadMore = list.size >= PAGE * PAGE_SIZE
                        adapter.refreshData(list)
                    }

                }, {
                    loading = false
                    mBinding.swipeLayout.isRefreshing = false

                    mViewModel.niceToast(getString(R.string.error_network_content))
                })
        mViewModel.mCompositeDisposable.add(subscribe)
    }

}