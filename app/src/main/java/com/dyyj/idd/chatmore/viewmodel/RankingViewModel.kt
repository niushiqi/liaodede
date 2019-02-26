package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.user.fragment.RankBankFragment
import com.dyyj.idd.chatmore.ui.user.fragment.RankPopularFragment
import com.dyyj.idd.chatmore.ui.user.fragment.RankRedFragment

class RankingViewModel: ViewModel() {
    /**
     * 标题
     */
    private val mTitles = arrayOf("银行榜", "人气榜", "红包榜")

    /**
     * Fragments
     */
    private val mFragment: Array<Fragment> = arrayOf(RankBankFragment(), RankPopularFragment(),
            RankRedFragment())

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)
}