package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.wallet.fragment.CashFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.GameToolsFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.GiftsReceivedFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.GoldFragment

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/22
 * desc   :
 */
class WalletViewModel : ViewModel() {

  /**
   * 标题
   */
  private val mTitles = arrayOf("礼物", "道具", "金币", "现金")

  /**
   * Fragments
   */
  private val mFragment: Array<Fragment> = arrayOf(GiftsReceivedFragment(),GameToolsFragment(), GoldFragment(),
                                                   CashFragment())

  /**
   * 获取Adapter
   */
  fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)
}