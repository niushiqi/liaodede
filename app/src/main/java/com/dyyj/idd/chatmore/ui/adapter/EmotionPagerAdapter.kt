package com.dyyj.idd.chatmore.ui.adapter

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.GridView

/**
 * Created by wangbin on 2018/11/10.
 */

class EmotionPagerAdapter(private val gvs: List<GridView>) : PagerAdapter() {

    override fun getCount(): Int {
        return gvs.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(gvs[position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        (container as ViewPager).addView(gvs[position])
        return gvs[position]
    }
}