package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.wallet.fragment.ReceivedGiftsFragment
import com.dyyj.idd.chatmore.ui.wallet.fragment.SendGiftsFragment

/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/05/22
 * desc   :
 */
class SharedRoseViewModel : ViewModel() {

  /**
   * 标题
   */
  private val mTitles = arrayOf("收到的礼物", "送出的礼物")

  /**
   * Fragments
   */
  private val mFragment: Array<Fragment> = arrayOf(ReceivedGiftsFragment(), SendGiftsFragment())

  /**
   * 获取Adapter
   */
  fun getAdapter(fm: FragmentManager) = PagerAdapterV2(fm, mFragment, mTitles)
}