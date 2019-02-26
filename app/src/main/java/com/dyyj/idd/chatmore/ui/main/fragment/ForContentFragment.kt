package com.dyyj.idd.chatmore.ui.main.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentForContentBinding
import com.dyyj.idd.chatmore.ui.main.activity.MainActivity
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaFragment
import com.dyyj.idd.chatmore.viewmodel.ForContentViewModel

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 消息下拉界面
 */
class ForContentFragment : BaseFragment<FragmentForContentBinding, ForContentViewModel>() {
    companion object {
        var mInstance: ForContentFragment? = null
        /**
         * 单例
         */
        fun instance(): ForContentFragment {
            if (mInstance == null) {
                return ForContentFragment()
            }
            return mInstance!!
        }
    }

    var currentFragment = -1

    override fun onLayoutId(): Int {
        return R.layout.fragment_for_content
    }

    override fun onViewModel(): ForContentViewModel {
        return ForContentViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initListener()
        selectFragment(0)
    }

    private fun initListener() {
        mBinding.tvBack.setOnClickListener {
            (activity as MainActivity).hideFragment2()
        }
        mBinding.tvChat.setOnClickListener {
            selectFragment(1)
        }
        mBinding.tvPlaza.setOnClickListener {
            selectFragment(0)
        }
    }

    private fun selectFragment(index: Int) {
        if (currentFragment == index) {
            return
        }

        currentFragment = index
        val isChat = index == 1

        if (isChat) showFragment(SystemMessageFragment.instance())
        if (!isChat) showFragment(PlazaFragment.instance())

        mBinding.tvPlaza.isSelected = !isChat
        mBinding.ivPlaza.isSelected = !isChat

        mBinding.tvChat.isSelected = isChat
        mBinding.ivChat.isSelected = isChat
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

    fun showFragment(fragment: Fragment) {
        val beginTransaction = childFragmentManager.beginTransaction()
        beginTransaction.replace(R.id.content, fragment)

        try {
            beginTransaction.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}