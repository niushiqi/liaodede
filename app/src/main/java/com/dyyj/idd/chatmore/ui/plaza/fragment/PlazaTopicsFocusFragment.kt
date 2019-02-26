package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaTopicsBinding
import com.dyyj.idd.chatmore.model.network.result.PlazaTopicListResult
import com.dyyj.idd.chatmore.viewmodel.PlazaTopicsFollowViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 话题列表
 */
class PlazaTopicsFocusFragment : BaseFragment<FragmentPlazaTopicsBinding, PlazaTopicsFollowViewModel>() {
    var PAGE = 1
    var loading = false

    var topItem: PlazaTopicListResult.Topic? = null

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza_topics
    }

    override fun onViewModel(): PlazaTopicsFollowViewModel {
        return PlazaTopicsFollowViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initRecycleView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateEvenBus(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        onDestryEvenBus(this)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lazyLoad()
    }

    override fun onStart() {
        super.onStart()
        netLoadList()
    }

    private fun initRecycleView() {
        mBinding.content.layoutManager = LinearLayoutManager(activity)
        mBinding.content.adapter = mViewModel.getAdapterFocus()

        mBinding.swipeLayout.setOnRefreshListener {
            PAGE = 1
            netLoadList()
        }
    }

    private fun netLoadList() {
        loading = true

        val subscribe = mViewModel.netTopicListByFocus().subscribe({
            mBinding.swipeLayout.isRefreshing = false
        }, {
            it.printStackTrace()
        })
        mViewModel.mCompositeDisposable.add(subscribe)
    }

    @Subscribe
    fun onSelectFocus(obj: PlazaTopicFocusPop.FocusVM) {
        mViewModel.unFollowTopic(obj.data)
    }

    @Subscribe
    fun onSelectTop(obj: PlazaTopicFocusPop.TopVM) {
        if (obj.data.top == 1) {
            mViewModel.revokeTopTopic(obj.data)
        } else {
            mViewModel.postTopTopic(obj.data)
        }
    }


}