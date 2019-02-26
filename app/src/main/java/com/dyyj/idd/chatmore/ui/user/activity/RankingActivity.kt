package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityRankingBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.RankingViewModel

class RankingActivity: BaseActivity<ActivityRankingBinding, RankingViewModel>() {
    companion object {

        fun start(context: Context) {
            val intent = Intent(context, RankingActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onLayoutId(): Int {
        return R.layout.activity_ranking
    }

    override fun onViewModel(): RankingViewModel {
        return RankingViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById<Toolbar>(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        mBinding.vp.offscreenPageLimit = 0
        mBinding.vp.adapter = mViewModel.getAdapter(supportFragmentManager)
        mBinding.tl4.setViewPager(mBinding.vp)
        mBinding.vp.offscreenPageLimit = 3
        mBinding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(position == 0) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_rankpage_banklist",""))
                } else if (position == 1) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_rankpage_popularitylist",""))
                } else if (position == 2) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_rankpage_redenveloplist",""))
                }
            }

        })
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "排行榜"

    }

}