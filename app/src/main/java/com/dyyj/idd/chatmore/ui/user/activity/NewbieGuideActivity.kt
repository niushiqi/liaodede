package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityNewbieGuideBinding
import com.dyyj.idd.chatmore.utils.DisplayUtils
import com.dyyj.idd.chatmore.viewmodel.NewbieGuideViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration

/**
 * Created by wangbin on 2019/1/19.
 */

class NewbieGuideActivity: BaseActivity<ActivityNewbieGuideBinding, NewbieGuideViewModel>() {
    companion object {
        fun start(context: Context) {
            var intent = Intent(context, NewbieGuideActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_newbie_guide
    }

    override fun onViewModel(): NewbieGuideViewModel {
        return NewbieGuideViewModel()
    }

    override fun onToolbar(): Toolbar? {
        return mBinding.layoutToolbar?.findViewById(R.id.toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arrays = arrayListOf<String>("ZenMeWan", "TiXian", "ShengDengJi")
        initRecycleView(arrays)
        mBinding.layoutToolbar?.findViewById<ImageView>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "新手指导"
    }

    fun initRecycleView(list: List<String>) {
        val decoration = HorizontalDividerItemDecoration.Builder(this@NewbieGuideActivity).color(
                ContextCompat.getColor(this@NewbieGuideActivity, android.R.color.transparent)).sizeResId(
                R.dimen.item_decoration_20dp).build()
        mViewModel.getAdapter().initData(list)
        mBinding.rvGuide.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvGuide.addItemDecoration(decoration)
        mBinding.rvGuide.layoutManager = LinearLayoutManager(this@NewbieGuideActivity)
        mBinding.rvGuide.adapter = mViewModel.getAdapter()
    }
}