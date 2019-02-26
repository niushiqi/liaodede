package com.dyyj.idd.chatmore.ui.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.dyyj.idd.chatmore.R
import com.dyyj.idd.chatmore.base.BaseActivity
import com.dyyj.idd.chatmore.databinding.ActivityContactLikeBinding
import com.dyyj.idd.chatmore.model.network.result.ContactUser
import com.dyyj.idd.chatmore.viewmodel.ContactLikeViewModel
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration

class ContactLikeActivity: BaseActivity<ActivityContactLikeBinding, ContactLikeViewModel>() {

    companion object {
        var mData: List<ContactUser>? = null
        fun start(context: Context, data: List<ContactUser>) {
            mData = data
            val intent = Intent(context, ContactLikeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onLayoutId(): Int {
        return R.layout.activity_contact_like
    }

    override fun onViewModel(): ContactLikeViewModel {
        return ContactLikeViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.layoutToolbar?.findViewById<TextView>(R.id.toolbar_title_tv)?.text = "已表白过的人"
        mBinding.layoutToolbar?.findViewById<View>(R.id.toolbar_back_iv)?.setOnClickListener { onBackPressed() }
        initRecycleView(mData!!)
    }

    fun initRecycleView(list: List<ContactUser>) {
        //设置线
        val decoration = HorizontalDividerItemDecoration.Builder(this).color(
                ContextCompat.getColor(this, R.color.divider_home)).sizeResId(
                R.dimen.item_decoration_2px).build()
        mViewModel.getAdapter().initData(list)
        mBinding.rvList.addItemDecoration(decoration)
        mBinding.rvList.addOnScrollListener(mViewModel.getAdapter().mScrollListener)
        mBinding.rvList.layoutManager = LinearLayoutManager(this)
        mBinding.rvList.adapter = mViewModel.getAdapter()

    }
}