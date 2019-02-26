package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.wallet.fragment.InviteLeftFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.InviteRightFragment

class InviteNewViewModel2: ViewModel() {
    /**
     * 标题
     */
    private val mTitles = arrayOf("邀新活动", "邀新记录")

    /**
     * Fragments
     */
    private val mFragment: Array<Fragment> = arrayOf(InviteLeftFragment(), InviteRightFragment())

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)
}