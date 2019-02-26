package com.dyyj.idd.chatmore.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.util.SparseArray
import android.view.ViewGroup


/**
 * author : yuhang
 * e-mail : 714610354@qq.com
 * time   : 2018/04/27
 * desc   : 首页
 */
class PagerAdapterV2( fm: FragmentManager, val array:Array<Fragment>, private val titles:Array<String>) : FragmentPagerAdapter(fm){
  private val mTags = SparseArray<String>()
  override fun getItem(position: Int): Fragment {
    return array[position]
  }

  override fun getCount(): Int {
    return array.size
  }

  override fun getPageTitle(position: Int): CharSequence? {
    return titles[position]
  }


  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    mTags.put(position, makeFragmentName(container.getId(), position));
    return super.instantiateItem(container, position)
  }

  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    mTags.remove(position);
    super.destroyItem(container, position, `object`)
  }

  private fun makeFragmentName(viewId: Int, position: Int): String {
    return "android:switcher:$viewId:$position"
  }
  private var mChildCount :Int = 0
  override fun notifyDataSetChanged() {
    mChildCount = count
    super.notifyDataSetChanged()
  }

  override fun getItemPosition(`object`: Any): Int {
    if (mChildCount > 0){
      mChildCount--
      return PagerAdapter.POSITION_NONE
    }
    return super.getItemPosition(`object`)
  }
}