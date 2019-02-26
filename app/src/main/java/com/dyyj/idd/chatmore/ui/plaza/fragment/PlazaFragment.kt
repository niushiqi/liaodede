package com.dyyj.idd.chatmore.ui.plaza.fragment

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseFragment
import com.dyyj.idd.chatmore.databinding.FragmentPlazaBinding
import com.dyyj.idd.chatmore.ui.plaza.activity.PlazaTopicsFocusActivity
import com.dyyj.idd.chatmore.viewmodel.PlazaViewModel
import org.greenrobot.eventbus.Subscribe

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   : 广场和圈子的首页
 */
class PlazaFragment : BaseFragment<FragmentPlazaBinding, PlazaViewModel>() {
    companion object {
        var mInstance: PlazaFragment? = null
        /**
         * 单例
         */
        fun instance(): PlazaFragment {
            if (mInstance == null) {
                return PlazaFragment()
            }
            return mInstance!!
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.fragment_plaza
    }

    override fun onViewModel(): PlazaViewModel {
        return PlazaViewModel()
    }

    override fun lazyLoad() {
        super.lazyLoad()
        initView()
        initListener()
    }

    private fun initListener() {
        mBinding.tv1.setOnClickListener {
            PlazaTopicsFocusActivity.start(context!!)
        }
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

    fun initView() {
        mBinding.vp.offscreenPageLimit = 0
        mBinding.vp.adapter = mViewModel.getAdapter(childFragmentManager)
        mBinding.tl4.setViewPager(mBinding.vp)
        mBinding.vp.offscreenPageLimit = 1
        mBinding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                //页面统计？
//                if(position == 0) {
//                    EventTrackingUtils.joinPoint(EventBeans("ck_wallet_propertypage",""))
//                } else if (position == 1) {
//                    EventTrackingUtils.joinPoint(EventBeans("ck_wallet_coinpage",""))
//                }
            }

        })
    }

}