package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaMainFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.GoldFragment

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   :
 */
class PlazaViewModel : ViewModel() {

    /**
     * 标题
     */
    private val mTitles = arrayOf("广场", "好友圈")

    /**
     * Fragments
     */
    private val mFragment: Array<Fragment> = arrayOf(PlazaMainFragment(), GoldFragment())

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)
}