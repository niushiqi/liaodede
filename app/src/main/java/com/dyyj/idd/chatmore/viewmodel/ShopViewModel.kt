package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.wallet.fragment.MatchingCardShopFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.PeasShopFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.StoneShopFragment

/**
 * author : malibo
 * time   : 2018/10/01
 * desc   :
 */
class ShopViewModel: ViewModel() {

    /**
     * 标题
     */
    private val mTitles = arrayOf("魔石", "得得豆","匹配卡")

    /**
     * Fragments
     */
    private val mFragment: Array<Fragment> = arrayOf(StoneShopFragment(), PeasShopFragment(), MatchingCardShopFragment())

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)

}