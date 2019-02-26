package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaTopicCardsBinding
import com.dyyj.idd.chatmore.viewmodel.PlazaPostedViewModel
import com.dyyj.idd.chatmore.viewmodel.PlazaTopicCardsViewModel
import com.gt.common.gtchat.extension.niceToast
import org.greenrobot.eventbus.Subscribe

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题 帖子列表
 */
class PlazaTopicCardsFragment : BaseFragment<FragmentPlazaTopicCardsBinding, PlazaTopicCardsViewModel>() {
    companion object {

        fun create(type: Int, topicID: String): PlazaTopicCardsFragment {
            val fragment = PlazaTopicCardsFragment()
            val bundle = Bundle()
            bundle.putInt("KEY_TYPE", type)
            bundle.putString("KEY_ID", topicID)
            fragment.arguments = bundle
            return fragment
        }
    }

    var lastVisibleItem = 0
    var PAGE = 1
    var PAGE_SIZE = 10
    var loading = false

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza_topic_cards
    }

    override fun onViewModel(): PlazaTopicCardsViewModel {
        return PlazaTopicCardsViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initRecycleView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateEvenBus(this)

        mViewModel.type = arguments!!.getInt("KEY_TYPE")
        mViewModel.topicID = arguments!!.getString("KEY_ID")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        onDestryEvenBus(this)
        super.onDestroyView()
    }

    private fun initRecycleView() {
        mViewModel.getAdapter()._type = mViewModel.type

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

        val plazaCardList =
                if (mViewModel.type == 0) mDataRepository.getTopicHotComment(mViewModel.topicID, page.toString(), pageSize.toString())
                else mDataRepository.getTopicNewComment(mViewModel.topicID, page.toString(), pageSize.toString())

        val subscribe = plazaCardList.subscribe(
                {
                    loading = false
                    mBinding.swipeLayout.isRefreshing = false

                    val adapter = mViewModel.getAdapter()
                    if (page != 1) {
                        adapter.moreData(it.data)
                        adapter.isLoadMore = adapter.getList().size >= PAGE * PAGE_SIZE

                    } else {
                        adapter.isLoadMore = it.data.size >= PAGE * PAGE_SIZE
                        adapter.refreshData(it.data)
                    }

                },
                {
                    loading = false
                    mBinding.swipeLayout.isRefreshing = false

                    mViewModel.niceToast(getString(R.string.error_network_content))
                }
        )
        mViewModel.mCompositeDisposable.add(subscribe)
    }

    @Subscribe
    fun onPubishSuccess(vm: PlazaPostedViewModel.PublishSuccess) {
        PAGE = 1
        loading = true
        netPlazaCardList(PAGE, PAGE_SIZE)
    }

}