package com.dyyj.idd.chatmore.viewmodel

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dyyj.idd.chatmore.base.ViewModel
import com.dyyj.idd.chatmore.ui.adapter.PagerAdapterV2
import com.dyyj.idd.chatmore.ui.plaza.fragment.PlazaTopicCardsFragment
import com.gt.common.gtchat.extension.niceToast
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus

/**
 * author : dengciping
 * e-mail : dengciping0716@gmail.com
 * time   : 2018/12/23
 * desc   :
 */
class PlazaTopicViewModel : ViewModel() {

    /**
     * 获取Adapter
     */
    fun getAdapter(fm: FragmentManager, id: String): PagerAdapterV2 {
        val mTitles = arrayOf("热门", "新帖")
        val mFragment: Array<Fragment> = arrayOf(PlazaTopicCardsFragment.create(0, id), PlazaTopicCardsFragment.create(1, id))
        return PagerAdapterV2(fm, mFragment, mTitles)
    }
}