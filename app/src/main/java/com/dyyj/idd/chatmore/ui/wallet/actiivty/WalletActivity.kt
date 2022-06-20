package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityWalletBinding
import com.dyyj.idd.chatmore.eventtracking.EventBeans
import com.dyyj.idd.chatmore.ui.user.activity.CashNoticeActivity
import com.dyyj.idd.chatmore.utils.EventTrackingUtils
import com.dyyj.idd.chatmore.viewmodel.WalletViewModel

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/22
 * desc   :
 */
class WalletActivity : BaseActivity<ActivityWalletBinding, WalletViewModel>() {

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, WalletActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_wallet
    }

    override fun onViewModel(): WalletViewModel {
        return WalletViewModel()
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
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.right_iv)?.text = "提现说明"
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.right_iv)?.visibility = View.GONE
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.right_iv)?.setOnClickListener {
            CashNoticeActivity.start(mBinding.root.context)
        }
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
                if (position == 3) {
                    mBinding.layoutToolbar?.findViewById<TextView>(R.id.right_iv)?.visibility = View.VISIBLE
                } else {
                    mBinding.layoutToolbar?.findViewById<TextView>(R.id.right_iv)?.visibility = View.GONE
                }
                if (position == 0) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_wallet_propertypage", ""))
                } else if (position == 1) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_wallet_coinpage", ""))
                } else if (position == 2) {
                    EventTrackingUtils.joinPoint(EventBeans("ck_wallet_cashpage", ""))
                }//TODO 礼物的埋点
            }

        })
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "我的钱包"

    }
}