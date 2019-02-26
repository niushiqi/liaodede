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
import com.dyyj.idd.chatmore.databinding.ActivityMyMsgBinding
import com.dyyj.idd.chatmore.ui.user.activity.RegisterUserInfoActivity.Companion.REQUEST_CODE_CHOOSE
import com.dyyj.idd.chatmore.viewmodel.MyMsgViewModel
import com.zhihu.matisse.Matisse
import org.greenrobot.eventbus.EventBus

class MyMsgActivity : BaseActivity<ActivityMyMsgBinding, MyMsgViewModel>() {
    companion object {
        const val SELECT_DEFAULT_PAGE = 0
        const val SELECT_SQUARE_PAGE = 0
        const val SELECT_CIRCLE_PAGE = 1
        const val SELECT_PAGE = "select_page"

        fun start(context: Context, selectPage: Int) {
            val intent = Intent(context, MyMsgActivity::class.java)
            intent.putExtra(SELECT_PAGE, selectPage)
            context.startActivity(intent)
        }
    }

    private fun getSelectPage() = intent.getIntExtra(SELECT_PAGE, SELECT_DEFAULT_PAGE)

    override fun onLayoutId(): Int {
        return R.layout.activity_my_msg
    }

    override fun onViewModel(): MyMsgViewModel {
        return MyMsgViewModel()
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
                if (position == 0) {
                    //EventTrackingUtils.joinPoint(EventBeans("ck_rankpage_banklist",""))
                } else if (position == 1) {
                    //EventTrackingUtils.joinPoint(EventBeans("ck_rankpage_popularitylist",""))
                }
            }
        })
        mBinding.vp.setCurrentItem(getSelectPage(), true)
    }

    private fun initListener() {
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "动态消息"
        mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
    }

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "动态消息"
        mToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        initRecycleView()
        mViewModel.netCircleList(mDataRepository.getUserid()!!, PAGE, 10)
        mViewModel.netUpdateMsg()
    }

    private fun initRecycleView() {
        mBinding.recyclerview.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.recyclerview.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerview.adapter = mViewModel.getAdapter()
//        mViewModel.getAdapter().mRealScrollListener = object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                //最后一个可见的ITEM
//                lastVisibleItem = (mBinding.recyclerview.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mViewModel.getAdapter().itemCount) {
//                    if (loadMore and !loading) {
//                        PAGE += 1
//                        loading = true
//                        mViewModel.netPlazaCardList(mDataRepository.getUserid()!!, PAGE, MyCircleActivity.PAGE_SIZE)
//                    }
//                }
//            }
//        }
    }

    @Subscribe
    fun onSubscribeCircle(obj: MyCircleMsgViewModel.MyCircleMsgVM) {
        loading = false
        if (obj.success) {
            if (obj.more) {
                mViewModel.getAdapter().moreData(obj?.vm!!)
                if (mViewModel.getAdapter().getList().size < PAGE * 10) {
                    loadMore = false
                }
            } else {
                loadMore = true
                mViewModel.getAdapter().refreshData(obj?.vm!!)
            }
        } else {
            niceToast(obj.message ?: "", Toast.LENGTH_SHORT)
        }
    }*/


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            println("+++++++++activity" + Matisse.obtainResult(data!!).size)
            EventBus.getDefault().post(Matisse.obtainPathResult(data))
        }
    }
}