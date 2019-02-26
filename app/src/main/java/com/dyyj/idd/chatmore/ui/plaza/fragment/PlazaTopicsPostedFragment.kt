package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaTopicsBinding
import com.dyyj.idd.chatmore.viewmodel.PlazaTopicsViewModel

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 发帖 话题列表
 */
class PlazaTopicsPostedFragment : BaseFragment<FragmentPlazaTopicsBinding, PlazaTopicsViewModel>() {
    var PAGE = 1
    var loading = false

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza_topics
    }

    override fun onViewModel(): PlazaTopicsViewModel {
        return PlazaTopicsViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initRecycleView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        onCreateEvenBus(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
//        onDestryEvenBus(this)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lazyLoad()
    }

    private fun initRecycleView() {
        mBinding.content.layoutManager = LinearLayoutManager(activity)
        mBinding.content.adapter = mViewModel.getAdapterPosted()

        mBinding.swipeLayout.setOnRefreshListener {
            PAGE = 1
            netLoadList()
        }

        netLoadList()
    }

    private fun netLoadList() {
        loading = true

        val subscribe = mViewModel.netTopicListByPosted().subscribe({
            mBinding.swipeLayout.isRefreshing = false
        }, {
            it.printStackTrace()
        })
        mViewModel.mCompositeDisposable.add(subscribe)
    }


}