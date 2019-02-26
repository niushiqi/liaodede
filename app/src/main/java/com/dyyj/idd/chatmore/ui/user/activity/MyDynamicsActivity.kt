package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityMyDynamicsBinding
import com.dyyj.idd.chatmore.viewmodel.MessageCircleViewModel
import com.dyyj.idd.chatmore.viewmodel.MyCircleViewModel
import com.dyyj.idd.chatmore.viewmodel.MyDynamicViewModel
import org.greenrobot.eventbus.Subscribe

class MyDynamicsActivity: BaseActivity<ActivityMyDynamicsBinding, MyDynamicViewModel>() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MyDynamicsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_my_dynamics
    }

    override fun onViewModel(): MyDynamicViewModel {
        return MyDynamicViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
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
        mBinding.vp.offscreenPageLimit = 2
        mBinding.vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(position == 0) {
                    //EventTrackingUtils.joinPoint(EventBeans("ck_rankpage_banklist",""))
                } else if (position == 1) {
                    //EventTrackingUtils.joinPoint(EventBeans("ck_rankpage_popularitylist",""))
                }
            }
        })
    }

    private fun initListener() {
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "我的动态"
        mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.GONE
        mToolbar?.findViewById<ImageView>(R.id.right_iv)?.setOnClickListener {
            MyMsgActivity.start(this, MyMsgActivity.SELECT_DEFAULT_PAGE)
        }

    }

    @Subscribe
    fun onClearUnMsg(obj: MessageCircleViewModel.ClearUnMsgVM) {
        if (obj.success) {
            mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.GONE
        }
    }

    @Subscribe
    fun onSubscribeUnMsg(obj: MyCircleViewModel.MyUnReadMsgVM) {
        if (obj.success) {
            if (obj.obj.unReadCount ?: 0 > 0) {
                mToolbar?.findViewById<ImageView>(R.id.right_iv_new)?.visibility = View.VISIBLE
            }
        }
    }

}