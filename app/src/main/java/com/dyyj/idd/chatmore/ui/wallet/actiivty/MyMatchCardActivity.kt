package com.dyyj.idd.chatmore.ui.wallet.actiivty

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityMyMatchCardBinding
import com.dyyj.idd.chatmore.ui.adapter.MyMatchCardAdapter
import com.dyyj.idd.chatmore.viewmodel.MyMatchCardViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import org.greenrobot.eventbus.Subscribe

/**
 * author : zwj
 * time   : 2019/01/23
 * desc   : 我的匹配卡
 */

class MyMatchCardActivity : BaseActivity<ActivityMyMatchCardBinding, MyMatchCardViewModel>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MyMatchCardActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_my_match_card
    }

    override fun onViewModel(): MyMatchCardViewModel {
        return MyMatchCardViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initListener()
        initRecycleView()
        mViewModel.matchCardList()
    }

    private fun initView() {
    }

    fun initRecycleView() {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(this@MyMatchCardActivity).color(
                ContextCompat.getColor(this@MyMatchCardActivity, R.color.divider_home)).sizeResId(
                R.dimen.item_home_decoration_15).build()
        mBinding.recyclerview.layoutManager = LinearLayoutManager(this@MyMatchCardActivity)
    }

    private fun initListener() {
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "我的匹配卡"
    }

    @Subscribe
    fun onMatchCardVM(obj: MyMatchCardViewModel.MatchCardVM) {
        if (obj.isSuccess) {
            mBinding.recyclerview.adapter = MyMatchCardAdapter(obj.obj)
        }
    }

}