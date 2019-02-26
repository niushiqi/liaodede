package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.user.fragment.MessageCircleFragment
import com.dyyj.idd.chatmore.ui.user.fragment.MessageSquareFragment

/**
 * Created by wangbin on 2018/12/26.
 */
class MyMsgViewModel: ViewModel() {
    /**
     * 标题
     */
    private val mTitles = arrayOf("广场", "好友圈")

    /**
     * Fragments
     */
    private val mFragment: Array<Fragment> = arrayOf(MessageSquareFragment(), MessageCircleFragment())

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)
}