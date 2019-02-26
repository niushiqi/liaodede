package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.user.activity.MyCircleFragment
import com.dyyj.idd.chatmore.ui.user.fragment.DynamicSquareFragment
import com.dyyj.idd.chatmore.ui.user.fragment.MessageSquareFragment

class MyDynamicViewModel: ViewModel() {
    /**
     * 标题
     */
    private val mTitles = arrayOf("广场", "好友圈")

    /**
     * Fragments
     */
    private val mFragment: Array<Fragment> = arrayOf(DynamicSquareFragment(), MyCircleFragment())

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)
}

